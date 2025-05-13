package kh.gangnam.b2b.webSocket;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@Slf4j
@Component
@RequiredArgsConstructor
public class WebSocketEventListener {

    private final WebSocketSessionManager sessionManager;

    // 클라이언트가 웹소켓 연결 시 호출
    @EventListener
    public void handleWebSocketConnectListener(SessionConnectedEvent event){
        StompHeaderAccessor headerAccessor=StompHeaderAccessor.wrap(event.getMessage());
        String sessionId=headerAccessor.getSessionId();
        String username=(String) headerAccessor.getSessionAttributes().get("username");

        if(username != null){
            sessionManager.addSession(username, sessionId);
            log.info("웹소켓 연결 성공: 사용자={}, 세션={}", username, sessionId);
        }
    }

    // 연결 종료 시 호출
    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event){
        StompHeaderAccessor headerAccessor=StompHeaderAccessor.wrap(event.getMessage());
        String sessionId=headerAccessor.getSessionId();
        sessionManager.removeSession(sessionId);
        log.info("웹소켓 연결 종료: 세션={}", sessionId);
    }

}
