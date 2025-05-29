package kh.gangnam.b2b.WebSocket;

import org.springframework.http.server.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

import java.security.Principal;
import java.util.Map;

@Component
public class CustomHandshakeHandler extends DefaultHandshakeHandler {

    @Override
    protected Principal determineUser(ServerHttpRequest request, WebSocketHandler wsHandler, Map<String, Object> attributes) {
        // JwtHandshakeInterceptor에서 저장한 username을 꺼냄
        String username = (String) attributes.get("username");

        if (username != null) {
            return () -> username; // 익명 구현체로 Principal 반환
        }

        return null;
    }

}
