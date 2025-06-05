package kh.gangnam.b2b.security;

import kh.gangnam.b2b.config.security.CustomEmployeeDetails;
import kh.gangnam.b2b.config.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Map;
import java.util.Set;


/**
 * WebSocket 연결 시 쿠키에서 access 토큰을 추출해 검증하는 인터셉터
 */
@RequiredArgsConstructor
@Component
public class StompJwtChannelInterceptor implements ChannelInterceptor {

    private final JwtTokenProvider jwtTokenProvider;
    @Value("${jwt.cookie.access-name}")
    private String accessTokenCookieName;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);

        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            // 1. 핸드셰이크 시 저장된 토큰 추출 (세션 속성에서 가져옴)
            Map<String, Object> sessionAttributes = accessor.getSessionAttributes();
            // 핸드셰이크 시 저장된 토큰 추출
            String access = (sessionAttributes != null) ?
                    (String) sessionAttributes.get(accessTokenCookieName) : null;

            if (access != null && jwtTokenProvider.validateToken(access)) {

                // 인증 정보 생성
                Long employeeId = jwtTokenProvider.getEmployeeId(access);
                String loginId = jwtTokenProvider.getLoginId(access);
                String role = jwtTokenProvider.getRole(access);
                String name = jwtTokenProvider.getRealName(access);
                CustomEmployeeDetails userDetails = new CustomEmployeeDetails(employeeId, loginId, name, role);

                // 권한 정보 생성
                Set<GrantedAuthority> authorities = jwtTokenProvider.getAuthoritiesFromToken(access);
                // 4. 인증 객체 생성
                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                authorities
                        );

                // 5. SecurityContext 설정
                SecurityContext context = SecurityContextHolder.createEmptyContext();
                context.setAuthentication(authToken);
                SecurityContextHolder.setContext(context);

                // 6. STOMP 세션에 Principal 저장
                accessor.setUser(authToken); // userDetails로 변경
            }
        }
        return message;
    }

    /**
     * 쿠키 헤더에서 특정 토큰명(accessToken, refreshToken 등)의 값을 추출
     */
    private String extractTokenFromCookie(StompHeaderAccessor accessor) {
        String cookieHeader = accessor.getFirstNativeHeader("cookie");
        if (cookieHeader == null) return null;

        return Arrays.stream(cookieHeader.split(";\\s*"))  // 세미콜론+공백으로 분리
                .map(cookie -> {
                    String[] parts = cookie.split("=", 2);  // 첫 번째 '='만 분할
                    return Map.entry(
                            parts[0].trim(),
                            parts.length > 1 ? parts[1].trim() : ""
                    );
                })
                .filter(entry -> entry.getKey().equals(accessTokenCookieName))
                .map(Map.Entry::getValue)
                .findFirst()
                .orElse(null);
    }
}
