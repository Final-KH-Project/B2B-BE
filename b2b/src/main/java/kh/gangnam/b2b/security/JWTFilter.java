package kh.gangnam.b2b.security;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kh.gangnam.b2b.dto.auth.CustomEmployeeDetails;
import kh.gangnam.b2b.dto.auth.Role;
import kh.gangnam.b2b.entity.auth.Employee;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;

@Slf4j
public class JWTFilter extends OncePerRequestFilter {

    private final JWTUtil jwtUtil;

    public JWTFilter(JWTUtil jwtUtil) {

        this.jwtUtil = jwtUtil;
    }


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String uri = request.getRequestURI();

        // 인증이 필요 없는 경로는 바로 다음 필터로 넘김
        if (uri.startsWith("/api/auth/login") || uri.startsWith("/api/auth/join")) {
            filterChain.doFilter(request, response);
            return;
        }

        // 쿠키에서 access 를 찾음
        String access = null;
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("access")) {
                    access = cookie.getValue();
                    System.out.println("Received Cookies: " + Arrays.toString(request.getCookies()));
                }
            }
        }
        // 토큰 검증 로직 수정
        if (access == null) {
            log.warn("[TOKEN] No access token found. URI: {}", request.getRequestURI());
            filterChain.doFilter(request, response);
            return;
        }


        // 토큰 만료 여부 확인, 만료시 다음 필터로 넘기지 않음
        try {
            jwtUtil.validateExpiration(access);
        } catch (ExpiredJwtException e) {
            String loginId = null;
            try {
                loginId = jwtUtil.getLoginId(access);
            } catch (Exception ignore) {}
            log.warn("[TOKEN] Access token expired. loginId: {}, URI: {}", loginId, request.getRequestURI());

            PrintWriter writer = response.getWriter();
            writer.print("access token expired");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        } catch (Exception e) {
            log.error("[TOKEN] Invalid access token. Error: {}, URI: {}", e.getMessage(), request.getRequestURI());
            PrintWriter writer = response.getWriter();
            writer.print("invalid access token");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        // username, role 획득
        String loginId = jwtUtil.getLoginId(access);
        Long employeeId = jwtUtil.getEmployeeId(access);
        String role = jwtUtil.getRole(access);

        // Role 유효성 검증
        if (role == null || !isValidRole(role)) {
            log.warn("[ROLE] Invalid role: {}. LoginID: {}, URI: {}", role, loginId, request.getRequestURI());
            sendErrorResponse(response, "invalid role", HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        Employee employee = new Employee();
        employee.setLoginId(loginId);
        employee.setRole(role);
        employee.setEmployeeId(employeeId);
        CustomEmployeeDetails customEmployeeDetails = new CustomEmployeeDetails(employee);

        Authentication authToken = new UsernamePasswordAuthenticationToken(
                customEmployeeDetails,
                null,
                customEmployeeDetails.getAuthorities());

        SecurityContextHolder.getContext().setAuthentication(authToken);

        filterChain.doFilter(request, response);
    }
    private boolean isValidRole(String role) {
        return Role.from(role) != null;
    }

    // 에러 응답 메서드
    private void sendErrorResponse(HttpServletResponse response, String message, int statusCode) throws IOException {
        response.setStatus(statusCode);
        response.setContentType("text/plain;charset=UTF-8");
        PrintWriter writer = response.getWriter();
        writer.print(message);
        writer.flush();
    }
}
