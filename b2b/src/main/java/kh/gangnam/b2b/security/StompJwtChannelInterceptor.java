package kh.gangnam.b2b.security;

import io.jsonwebtoken.Claims;
import kh.gangnam.b2b.dto.auth.CustomUserDetails;
import kh.gangnam.b2b.entity.auth.User;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;

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

                    // 3. Principal(CustomUserDetails) 생성
                    String username = claims.get("username", String.class);
                    // userId는 Integer 또는 Long일 수 있으니 타입 변환 주의
                    Object userIdObj = claims.get("userId");
                    Long userId = userIdObj instanceof Integer
                            ? ((Integer) userIdObj).longValue()
                            : (Long) userIdObj;
                    String role = claims.get("role", String.class);

                    // User 엔티티 생성 (필요한 필드만)
                    User user = new User();
                    user.setUserId(userId);
                    user.setUsername(username);
                    user.setRole(role);

                    // CustomUserDetails 생성 (Principal 구현체)
                    CustomUserDetails userDetails = new CustomUserDetails(user);

                    // 4. Principal 세팅 (★ 이 부분이 핵심!)
                    accessor.setUser(userDetails); // CustomUserDetails가 Principal 구현체여야 함

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
