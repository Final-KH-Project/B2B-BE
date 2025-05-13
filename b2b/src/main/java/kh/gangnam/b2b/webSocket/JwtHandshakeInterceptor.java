package kh.gangnam.b2b.webSocket;



import kh.gangnam.b2b.security.JWTUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Component // 스프링 빈으로 등록
public class JwtHandshakeInterceptor implements HandshakeInterceptor {

    private final JWTUtil jwtUtil;

    @Override
    public boolean beforeHandshake(
            ServerHttpRequest request,
            ServerHttpResponse response,
            WebSocketHandler wsHandler,
            Map<String, Object> attributes
    ) {

        // 1. HttpServletRequest 로 형변환
        if (!(request instanceof ServletServerHttpRequest)) {
            return false;
        }

        HttpServletRequest servletRequest = ((ServletServerHttpRequest) request).getServletRequest();

        // 2. 쿠키에서 access_token 꺼내기
        String token = null;
        if (servletRequest.getCookies() != null) {
            for (Cookie cookie : servletRequest.getCookies()) {
                if ("access_token".equals(cookie.getName())) {
                    token = cookie.getValue();
                    break;
                }
            }
        }

        if (token == null) {
            log.warn("WebSocket 연결 실패: access_token 쿠키 없음");
            return false;
        }

        // 3. 토큰 유효성 검사
        try {
            if (jwtUtil.isExpired(token)) {
                log.warn("WebSocket 연결 실패: JWT 토큰 만료");
                return false;
            }

            String username = jwtUtil.getUsername(token);
            attributes.put("username", username); // 세션 속성에 저장 (WebSocket에서 사용 가능)
            return true;

        } catch (Exception e) {
            log.error("WebSocket 연결 실패: JWT 토큰 파싱 실패", e);
            return false;
        }
    }

    @Override
    public void afterHandshake(
            ServerHttpRequest request,
            ServerHttpResponse response,
            WebSocketHandler wsHandler,
            Exception exception
    ) {
        // 핸드셰이크 후 로직 필요 시 작성
    }
} //HandShaker Interceptor: 세션 정보를 가져 옴, 로그인 된 사용자만 웹소켓을 이용가능 하게 함
