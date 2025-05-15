package kh.gangnam.b2b.security;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kh.gangnam.b2b.dto.auth.CustomEmployeeDetails;
import kh.gangnam.b2b.entity.auth.Employee;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;

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
                    System.out.println("Received Cookies: " + Arrays.toString(request.getCookies()));
                    if (access == null) {
                        System.out.println("Access token is null!");
                    } else {
                        System.out.println("Access token found: " + access);
                    }
                }
            }
        }
        // 토큰 검증 로직 수정
        if (access == null) {
            System.out.println("access token null");
            filterChain.doFilter(request, response);

            // 토근이 없으면 조건이 해당되면 메소드 종료 (필수)
            return;
        }


        // 토큰 만료 여부 확인, 만료시 다음 필터로 넘기지 않음
        try {
            jwtUtil.isExpired(access);
        } catch (ExpiredJwtException e) {

            //response body
            PrintWriter writer = response.getWriter();
            writer.print("access token expired");

            //response status code
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }
        // 토큰이 access인지 확인 (발급시 페이로드에 명시)
        String category = jwtUtil.getCategory(access);

        if (!category.equals("access")) {

            //response body
            PrintWriter writer = response.getWriter();
            writer.print("invalid access token");

            //response status code
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
