package kh.gangnam.b2b.WebSocket;

import kh.gangnam.b2b.entity.alarm.Alarm;
import kh.gangnam.b2b.service.AlarmService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.security.Principal;
import java.util.Collections;
import java.util.List;


@Slf4j
@Component
@RequiredArgsConstructor
public class WebSocketEventListener {

    private final WebSocketSessionManager sessionManager;
    private final AlarmService alarmService;
    private final SimpMessagingTemplate messagingTemplate;


    // 클라이언트가 웹소켓 연결 시 호출
    @EventListener
    public void handleConnect(SessionConnectedEvent event){
        StompHeaderAccessor headerAccessor=StompHeaderAccessor.wrap(event.getMessage());
        String sessionId=headerAccessor.getSessionId();

        //String username=(String) headerAccessor.getSessionAttributes().get("username");
        Principal userPrincipal=headerAccessor.getUser();
        if(userPrincipal != null){
            String username=userPrincipal.getName();
            sessionManager.addSession(username, sessionId);
            log.info("웹소켓 연결 성공: 사용자={}, 세션={}", username, sessionId);

            //알림 조회 후 사용자에게 전송
            List<Alarm> unreadAlarms = alarmService.getUnreadAlarmsByUsername(username);
            if (unreadAlarms == null) {
                unreadAlarms = Collections.emptyList(); // java.util.Collections
            }
            for (Alarm alarm : unreadAlarms) {
                String message = "새로운 알림이 있습니다.";

                log.info("알림 전송: {}, 세션: {}", message, sessionId);
                messagingTemplate.convertAndSendToUser(
                        username,
                        "/queue/message", //클라이언트 구독 경로와 일치해야 됨
                        AlarmMessage.builder()
                                .message(message)
                                .build()
                );
                log.info("알림 전송 완료");
            }

        } else {
            log.warn("웹소켓 연결 : 사용자 정보를 찾을 수 없음. sesstionId={}", sessionId);
        }
    }

    // 연결 종료 시 호출
    public void handleDisconnect(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = headerAccessor.getSessionId();
        sessionManager.removeSession(sessionId);
        log.info("웹소켓 연결 종료: 세션={}", sessionId);
    }

}
