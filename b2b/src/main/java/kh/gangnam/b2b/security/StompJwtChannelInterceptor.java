package kh.gangnam.b2b.security;

import io.jsonwebtoken.Claims;
import kh.gangnam.b2b.dto.auth.CustomEmployeeDetails;
import kh.gangnam.b2b.entity.auth.Employee;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;

import java.security.Principal;

/**
 * WebSocket 연결 시 쿠키에서 access 토큰을 추출해 검증하는 인터셉터
 */
@RequiredArgsConstructor
@Component
public class StompJwtChannelInterceptor implements ChannelInterceptor {

    private final JWTUtil jwtUtil;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);

        // WebSocket CONNECT 시점에만 토큰 검증
        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            // 1. 쿠키 헤더에서 access 토큰 추출
            String cookieHeader = accessor.getFirstNativeHeader("cookie");
            if (cookieHeader != null) {
                String access = extractTokenFromCookie(cookieHeader, "accessToken");
                if (access != null) {
                    // 2. JWT 검증 (유효하지 않으면 예외 발생)
                    Claims claims = jwtUtil.validateAndParseClaims(access);

                    // 3. Principal(CustomEmployeeDetails) 생성
                    String loginId = claims.get("loginId", String.class);
                    // userId는 Integer 또는 Long일 수 있으니 타입 변환 주의
                    Object employeeIdObj = claims.get("employeeId");
                    Long employeeId = employeeIdObj instanceof Integer
                            ? ((Integer) employeeIdObj).longValue()
                            : (Long) employeeIdObj;
                    String role = claims.get("role", String.class);

                    // Employee 엔티티 생성 (필요한 필드만)
                    Employee employee = new Employee();
                    employee.setEmployeeId(employeeId);
                    employee.setLoginId(loginId);
                    employee.setRole(role);

                    // CustomEmployeeDetails 생성 (Principal 구현체)
                    CustomEmployeeDetails employeeDetails = new CustomEmployeeDetails(employee);

                    // 4. Principal 세팅 (★ 이 부분이 핵심!)
                    accessor.setUser(employeeDetails); // CustomUserDetails가 Principal 구현체여야 함

                } else {
                    throw new IllegalArgumentException("Access Token not found in cookie");
                }
            } else {
                throw new IllegalArgumentException("Cookie header missing");
            }
        }
        return message;
    }

    /**
     * 쿠키 헤더에서 특정 토큰명(accessToken, refreshToken 등)의 값을 추출
     */
    private String extractTokenFromCookie(String cookieHeader, String tokenName) {
        // 예시: "accessToken=xxx; refreshToken=yyy"
        for (String cookie : cookieHeader.split(";")) {
            String[] parts = cookie.trim().split("=");
            if (parts.length == 2 && parts[0].equals(tokenName)) {
                return parts[1];
            }
        }
        return null;
    }
}
