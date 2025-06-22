package kh.gangnam.b2b.config.websocket;

import java.security.Principal;

import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@Slf4j
//@Component
@RequiredArgsConstructor
public class WebSocketEventListener {

    private final WebSocketSessionManager sessionManager;


    // 클라이언트가 웹소켓 연결 시 호출
    @EventListener
    public void handleWebSocketConnectListener(SessionConnectedEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = headerAccessor.getSessionId();

        log.info("=== WebSocket 연결 이벤트 ===");
        log.info("세션 ID: {}", sessionId);
        log.info("연결 시간: {}", headerAccessor.getTimestamp());

        // 연결된 세션 정보 로깅
        if (headerAccessor.getUser() != null) {
            Principal user = headerAccessor.getUser();
            log.info("연결된 사용자: {}", user.getName());
        }

        log.info("현재 연결된 세션 수: {}", sessionManager.getAllUsers().size());
    }

    // 클라이언트가 웹소켓 연결 해제 시 호출
    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = headerAccessor.getSessionId();

        log.info("=== WebSocket 연결 해제 이벤트 ===");
        log.info("세션 ID: {}", sessionId);
        log.info("해제 시간: {}", headerAccessor.getTimestamp());

        // 세션 관리자에서 제거
        sessionManager.removeSession(sessionId);

        // 연결된 사용자 정보 로깅
        if (headerAccessor.getUser() != null) {
            Principal user = headerAccessor.getUser();
            log.info("연결 해제된 사용자: {}", user.getName());
        }

        log.info("현재 연결된 세션 수: {}", sessionManager.getAllUsers().size());
        log.info("연결된 사용자 목록: {}", sessionManager.getAllUsers());
    }
}
