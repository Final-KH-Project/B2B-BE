package kh.gangnam.b2b.security;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import kh.gangnam.b2b.config.SecurityConstants;
import kh.gangnam.b2b.entity.auth.Employee;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final JwtCookieManager jwtCookieManager;
    private final RefreshTokenService refreshTokenService;
    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    @Value("${jwt.token-prefix}")
    private String TOKEN_PREFIX;

    @Value("${jwt.header}")
    private String HEADER_STRING;

    @Value("${jwt.cookie.access-name}")
    private String ACCESS_TOKEN_COOKIE_NAME;

    @Value("${jwt.cookie.refresh-name}")
    private String REFRESH_TOKEN_COOKIE_NAME;

    /**
     * 필터를 적용하지 않을 URL 패턴을 정의합니다.
     * SecurityConstants에 정의된 공개 URL에 대해서는 인증을 건너뜁니다.
     */
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        String method = request.getMethod();

        // GET 요청인 경우 공개 URL 체크
        if (HttpMethod.GET.name().equals(method)) {
            boolean shouldNotFilter = Arrays.stream(SecurityConstants.PUBLIC_GET_URLS)
                    .anyMatch(pattern -> pathMatcher.match(pattern, path));
            if (shouldNotFilter) {
                log.debug("[FILTER] Pass-through (GET): {}", path);
            }
            return shouldNotFilter;
        }

        // POST 요청인 경우 공개 URL 체크
        if (HttpMethod.POST.name().equals(method)) {
            boolean shouldNotFilter = Arrays.stream(SecurityConstants.PUBLIC_POST_URLS)
                    .anyMatch(pattern -> pathMatcher.match(pattern, path));
            if (shouldNotFilter) {
                log.debug("[FILTER] Pass-through (POST): {}", path);
            }
            return shouldNotFilter;
        }

        return false;
    }

    /**
     * JWT 토큰 기반 인증을 처리하는 메인 필터 로직입니다.
     * 1. Access Token 검증
     * 2. Access Token 만료 시 Refresh Token으로 갱신
     * 3. 인증 정보 설정
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            // 1. 요청 쿠키에서 JWT 토큰 추출
            String accessToken = jwtCookieManager.getAccessTokenFromCookie(request);

            // 2. 토큰이 존재하고 유효한 경우 인증 처리
            if (accessToken != null && jwtTokenProvider.validateToken(accessToken)) {
                log.info("[TOKEN] 유효한 Access Token. loginId: {}, URI: {}",
                        jwtTokenProvider.getLoginId(accessToken), request.getRequestURI());
                setAuthentication(accessToken, request);
            }
        } catch (ExpiredJwtException e) {
            // 3. Access Token이 만료된 경우 Refresh Token으로 갱신 시도
            log.warn("[TOKEN] Access Token 만료. URI: {}", request.getRequestURI());
            String refreshToken = jwtCookieManager.getRefreshTokenFromCookie(request);
            if (refreshToken != null) {
                try {
                    // 4. DB에서 기존 리프레시 토큰 존재 확인
                    if (!refreshTokenService.existsByRefresh(refreshToken)) {
                        log.warn("[TOKEN] Refresh Token DB에 없음. URI: {}", request.getRequestURI());
                        throw new JwtTokenValidationException("Refresh Token이 DB에 없습니다.");
                    }
                    // 5. access, refresh 새 토큰 발급
                    TokenResponse tokenResponse = jwtTokenProvider.refreshToken(refreshToken);

                    // 6. 기존 refresh token 삭제 및 새 토큰 저장
                    Long employeeId = jwtTokenProvider.getEmployeeId(tokenResponse.refreshToken());
                    refreshTokenService.deleteByEmployeeId(employeeId);
                    refreshTokenService.save(
                            employeeId,
                            tokenResponse.refreshToken());

                    // 7. 새로운 Access Token으로 인증 설정
                    log.info("[TOKEN] Access/Refresh Token 재발급 성공. employeeId: {}, URI: {}",
                            employeeId, request.getRequestURI());
                    setAuthentication(tokenResponse.accessToken(), request);

                    // 8. 새로운 Access Token을 쿠키에 설정
                    jwtCookieManager.setAccessTokenCookie(response, tokenResponse.accessToken());
                    jwtCookieManager.setRefreshTokenCookie(response, tokenResponse.refreshToken());

                } catch (Exception ex) {
                    log.error("[TOKEN] 토큰 갱신 실패. URI: {}", request.getRequestURI(), ex);
                    handleJwtException(response, new JwtTokenValidationException("토큰 갱신에 실패했습니다."));
                    return;
                }
            } else {
                log.warn("[TOKEN] Refresh Token 없음. URI: {}", request.getRequestURI());
                handleJwtException(response, new JwtTokenValidationException("Refresh Token이 없습니다."));
                return;
            }
        } catch (Exception ex) {
            log.error("[TOKEN] JWT 인증 처리 중 오류 발생. URI: {}", request.getRequestURI(), ex);
            handleJwtException(response, ex);
            return;
        }

        filterChain.doFilter(request, response);
    }

    /**
     * 토큰으로부터 인증 정보를 추출하고 SecurityContext에 설정합니다.
     * @param token JWT 토큰
     * @param request HTTP 요청 객체
     */
    private void setAuthentication(String token, HttpServletRequest request) {
        // 1. 토큰에서 사용자 정보 추출
        Long employeeId = jwtTokenProvider.getEmployeeId(token);
        String loginId = jwtTokenProvider.getLoginId(token);
        String role = jwtTokenProvider.getRole(token);
        Set<GrantedAuthority> authorities = jwtTokenProvider.getAuthoritiesFromToken(token);

        log.info("[AUTH] 인증 정보 설정. loginId: {}, employeeId: {}, role: {}, URI: {}",
                loginId, employeeId, role, request.getRequestURI());

        // 2. CustomEmployeeDetails 객체 생성
        Employee employee = new Employee();
        employee.setEmployeeId(employeeId);
        employee.setLoginId(loginId);
        employee.setRole(role);
        CustomEmployeeDetails userDetails = new CustomEmployeeDetails(employee);

        // 3. principal에 userDetails 저장
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(userDetails, null, authorities);
        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    /**
     * HTTP 요청 헤더에서 JWT 토큰을 추출합니다.
     * Authorization 헤더의 Bearer 토큰 형식을 처리합니다.
     * @param request HTTP 요청 객체
     * @return JWT 토큰 문자열, 없으면 null
     */
    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader(HEADER_STRING);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(TOKEN_PREFIX)) {
            return bearerToken.substring(7);
        }
        return null;
    }

    /**
     * JWT 관련 예외를 처리하고 클라이언트에게 JSON 형식의 에러 응답을 반환합니다.
     * @param response HTTP 응답 객체
     * @param e 발생한 예외
     * @throws IOException 응답 작성 중 발생할 수 있는 IO 예외
     */
    private void handleJwtException(HttpServletResponse response, Exception e) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json;charset=UTF-8");

        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("status", HttpServletResponse.SC_UNAUTHORIZED);
        errorResponse.put("error", "Unauthorized");

        if (e instanceof ExpiredJwtException) {
            log.warn("[TOKEN] JWT 토큰 만료", e);
            errorResponse.put("message", "토큰이 만료되었습니다");
        } else if (e instanceof JwtTokenValidationException) {
            log.error("[TOKEN] 유효하지 않은 토큰: {}", e.getMessage());
            errorResponse.put("message", e.getMessage());
        } else {
            log.error("[TOKEN] JWT 인증 처리 중 오류 발생", e);
            errorResponse.put("message", "인증에 실패했습니다");
        }

        response.getWriter().write(new ObjectMapper().writeValueAsString(errorResponse));
    }

}