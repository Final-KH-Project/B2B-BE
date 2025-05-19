package kh.gangnam.b2b.security;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kh.gangnam.b2b.dto.auth.CustomEmployeeDetails;
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

        // 쿠키에서 access 를 찾음
        String access = null;
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("access")) {
                    access = cookie.getValue();
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
}
