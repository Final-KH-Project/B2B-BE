package kh.gangnam.b2b.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kh.gangnam.b2b.dto.auth.request.LoginDTO;
import kh.gangnam.b2b.dto.auth.response.LoginResponse;
import kh.gangnam.b2b.entity.auth.Refresh;
import kh.gangnam.b2b.entity.auth.Employee;
import kh.gangnam.b2b.repository.RefreshRepository;
import kh.gangnam.b2b.repository.EmployeeRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Iterator;

public class LoginFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;
    private final JWTUtil jwtUtil;
    private final ObjectMapper objectMapper;
    private final RefreshRepository refreshRepository;
    private final Long accessExpired;
    private final Long refreshExpired;
    private final EmployeeRepository employeeRepository;



    public LoginFilter(AuthenticationManager authenticationManager, JWTUtil jwtUtil, ObjectMapper objectMapper, RefreshRepository refreshRepository, Long accessExpired, Long refreshExpired, EmployeeRepository employeeRepository) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.objectMapper = objectMapper;
        this.refreshRepository = refreshRepository;
        this.accessExpired = accessExpired;
        this.refreshExpired = refreshExpired;
        this.employeeRepository = employeeRepository;
        setFilterProcessesUrl("/api/auth/login");
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        try {
            // JSON body 파싱
            LoginDTO loginDTO = objectMapper.readValue(request.getInputStream(), LoginDTO.class);
            UsernamePasswordAuthenticationToken authToken =
                    new UsernamePasswordAuthenticationToken(loginDTO.getLoginId(), loginDTO.getPassword());
            return authenticationManager.authenticate(authToken);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authentication) throws IOException {
        //
        String loginId = authentication.getName();

        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        Iterator<? extends GrantedAuthority> iterator = authorities.iterator();
        GrantedAuthority auth = iterator.next();
        String role = auth.getAuthority();


        // userId 조회
        Employee employee = employeeRepository.findByLoginId(loginId);
        Long employeeId = employee.getEmployeeId();

        //토큰 생성
        // 3,600,000ms = 1시간
        String access = jwtUtil.createJwt("access", loginId, employeeId, role, accessExpired);
        LocalDateTime expiresAt = LocalDateTime.now().plusSeconds(accessExpired/1000);
        // 86,400,000ms = 하루
        String refresh = jwtUtil.createJwt("refresh", loginId, employeeId, role, refreshExpired);


        // LoginResponse 객체 생성
        LoginResponse loginResponse = LoginResponse.builder()
//                .accessToken(access)
                .expiresAt(expiresAt)
                .build();

        //Refresh 토큰 저장
        addRefreshEntity(loginId, refresh, expiresAt);

        // JSON 응답 설정
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        // 쿠키 설정
        response.addCookie(createCookie("access", access));
        response.addCookie(createCookie("refresh", refresh));
        response.setStatus(HttpServletResponse.SC_OK);
        try {
            objectMapper.writeValue(response.getWriter(), loginResponse);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException {
        response.setContentType("application/json");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.getWriter().write("{\"error\": \"Invalid username or password\"}");
    }

    private Cookie createCookie(String key, String value) {

        Cookie cookie = new Cookie(key, value);
        cookie.setMaxAge(key.equals("access") ? (int) (accessExpired / 1000) : (int) (refreshExpired / 1000));
        //cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setHttpOnly(true);

        return cookie;
    }

    /**
     * RefreshEntity 를 생성하고 저장하는 로직
     * @param username
     * @param refresh
     * @param expiredMs
     */
    private void addRefreshEntity(String username, String refresh, LocalDateTime expiredMs) {

        // 기존 토큰 삭제
        refreshRepository.findByLoginId(username)
                .ifPresent(refreshRepository::delete);

        // 새 토큰 저장
        Refresh refreshEntity = Refresh.builder()
                .loginId(username)
                .refresh(refresh)
                .expiresAt(expiredMs)
                .build();

        refreshRepository.save(refreshEntity);
    }
}