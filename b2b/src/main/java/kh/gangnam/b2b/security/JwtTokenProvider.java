package kh.gangnam.b2b.security;

import java.util.Collections;
import java.util.Date;
import java.util.Set;

import javax.crypto.SecretKey;

import kh.gangnam.b2b.entity.auth.Employee;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.Getter;

/**
 * JWT 토큰의 생성, 검증, 파싱을 담당하는 컴포넌트입니다.
 *
 * 주요 기능:
 * - 토큰 생성 (액세스 토큰, 리프레시 토큰)
 * - 토큰 검증 (서명, 만료 시간)
 * - 토큰 정보 추출 (사용자명, 권한 등)
 * - 토큰 갱신
 */
@Getter
@Component
public class JwtTokenProvider {

    private static final String ROLES_CLAIM = "roles";


    // JWT 서명에 사용할 비밀키
    @Value("${jwt.secret}")
    private String secretKey;

    // 액세스 토큰 만료 시간 (application 에서 주입)
    // 로그인 성공 시 액세스 토큰 생성에 사용
    @Value("${jwt.token.access-expiration}")
    private long accessExpirationMs;

    // 리프레시 토큰 만료 시간 (application에서 주입)
    // 로그인 성공 시 리프레시 토큰 생성에 사용
    @Value("${jwt.token.refresh-expiration}")
    private long refreshExpirationMs;

    // JWT 서명에 사용할 SecretKey 생성
    // 토큰 생성 및 검증 시 사용
    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secretKey.getBytes());
    }

    // 토큰에서 사용자 정보 추출
    // 토큰 검증 시 사용자 식별에 사용
    // 토큰에서 employeeId 추출 (주체로부터)
    public String getLoginId(String token) {
        return extractAllClaims(token).getSubject();
    }

    // 사용자명 대신 LoginId 반환 (Spring Security 호환)
    public String getUsernameFromToken(String token) {
        return getLoginId(token);
    }

    public Long getEmployeeId(String token) {
        return extractAllClaims(token).get("employeeId", Long.class);
    }

    public String getRole(String token) {
        return extractAllClaims(token).get("role", String.class);
    }

    public String getCategory(String token) {
        return extractAllClaims(token).get("category", String.class);
    }

    // 토큰에서 만료 시간 추출
    // 토큰 만료 여부 확인 시 사용
    public Date extractExpiration(String token) {
        return extractAllClaims(token).getExpiration();
    }

    /**
     * 토큰에서 모든 클레임 추출
     * 토큰 검증 및 정보 추출 시 사용
     * @param token
     * @return
     */
    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    // 토큰 만료 여부 확인
    // 토큰 검증 시 만료 여부 확인에 사용
    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    /**
     * 새로운 액세스 토큰을 생성합니다.
     *
     * @param authentication 인증 정보
     * @return 생성된 액세스 토큰 type: String
     */
    public String generateAccessToken(Authentication authentication) {
        return createToken(authentication, accessExpirationMs, "access");
    }

    /**
     * 새로운 리프레시 토큰을 생성합니다.
     *
     * @param authentication 인증 정보
     * @return 생성된 액세스 토큰 type: String
     */
    public String generateRefreshToken(Authentication authentication) {
        return createToken(authentication, refreshExpirationMs, "refresh");
    }

    /**
     * 토큰 생성 시 발생할 수 있는 예외를 처리합니다.
     * @param authentication 인증 정보
     * @param expirationMs 인증 시간
     * @param category 토큰 Type (access, refresh)
     * @return 생성된 토큰
     * @throws IllegalArgumentException 인증 정보가 유효하지 않은 경우
     */
    private String createToken(Authentication authentication, long expirationMs, String category) {
        // 1. 인증 객체 유효성 검사
        if (authentication == null) {
            throw new IllegalArgumentException("Authentication 객체가 null입니다.");
        }

        // 2. Principal 유효성 검사
        Object principal = authentication.getPrincipal();
        if (!(principal instanceof CustomEmployeeDetails userDetails)) {
            throw new IllegalArgumentException("잘못된 Principal 타입입니다.");
        }

        return Jwts.builder()
                .subject(userDetails.getUsername())  // 주체(Subject)를 employeeId로 설정
                .claim("category", category)
                .claim("loginId", userDetails.getUsername())
                .claim("employeeId", userDetails.getEmployeeId())
                .claim("role", userDetails.getRole())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expirationMs))
                .signWith(getSigningKey())
                .compact();
    }

    /**
     * 토큰의 유효성을 검증합니다.
     * 서명과 만료 시간을 확인합니다.
     *
     * @param token 토큰
     * @return 토큰 유효 시 true, 아니면 false
     * @throws JwtTokenValidationException 토큰이 만료된 경우, 토큰이 유효하지 않은 경우
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token);
            return !isTokenExpired(token);
        } catch (ExpiredJwtException ex) {
            throw new JwtTokenValidationException("Token expired");
        } catch (JwtException | IllegalArgumentException ex) {
            throw new JwtTokenValidationException("Invalid token");
        }
    }

    /**
     * JWT 토큰에서 권한 정보를 추출합니다.
     *
     * @param token JWT 토큰
     * @return 단일 권한
     */
    public Set<GrantedAuthority> getAuthoritiesFromToken(String token) {
        // 1. 역할 정보 추출 (단일 역할)
        String role = extractAllClaims(token).get("role", String.class);

        // 2. 역할이 없는 경우 빈 Set 반환
        if (role == null || role.isEmpty()) {
            return Collections.emptySet();
        }

        // 3. 단일 역할을 GrantedAuthority로 변환
        return Collections.singleton(new SimpleGrantedAuthority(role));
    }

    public TokenResponse refreshToken(String refreshToken) {
        try {
            // 1. 토큰 자체 유효성 검사
            if (!validateToken(refreshToken)) {
                throw new JwtTokenValidationException("유효하지 않은 refresh token 입니다. ");
            }

            // 2. 새로운 인증 객체 생성
            CustomEmployeeDetails userDetails = new CustomEmployeeDetails(
                    getEmployeeId(refreshToken),
                    getLoginId(refreshToken),
                    getRole(refreshToken)
            );
            Authentication authentication = new UsernamePasswordAuthenticationToken(
                    userDetails, null, userDetails.getAuthorities()
            );

            // 3. 새로운 access & refresh token 생성
            String newAccessToken = generateAccessToken(authentication);
            String newRefreshToken = generateRefreshToken(authentication);

            return TokenResponse.of(newAccessToken, newRefreshToken, accessExpirationMs);
        } catch (ExpiredJwtException ex) {
            throw new JwtTokenValidationException("만료된 리프레시 토큰");
        } catch (Exception ex) {
            throw new JwtTokenValidationException("토큰 갱신 실패: " + ex.getMessage());
        }
    }
}