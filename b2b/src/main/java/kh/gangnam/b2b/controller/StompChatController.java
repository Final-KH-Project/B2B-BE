package kh.gangnam.b2b.controller;

import kh.gangnam.b2b.config.security.CustomEmployeeDetails;
import kh.gangnam.b2b.dto.chat.request.SendChat;
import kh.gangnam.b2b.dto.chat.response.ChatMessages;
import kh.gangnam.b2b.service.ChatWebSocketService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.socket.messaging.SessionConnectedEvent;

import java.util.Map;

/**
 * WebSocket 채팅 메시지 송수신 컨트롤러
 * - 클라이언트가 /pub/chat/message로 메시지 발행 시 동작
 * - 처리 후 /sub/chat/room/{roomId}로 메시지 브로드캐스트
 */
@Controller
@RequiredArgsConstructor
@Slf4j
public class StompChatController {
    //브로커 추가해야함
    private final SimpMessagingTemplate messagingTemplate;
    private final ChatWebSocketService chatWebSocketService;

    @MessageMapping("/chat/messages")
    public void message(@Payload SendChat message,
                        Authentication authentication) {

        CustomEmployeeDetails userDetails = (CustomEmployeeDetails) authentication.getPrincipal();
        if (userDetails == null) {
            throw new AuthenticationCredentialsNotFoundException("인증 필요");
        }

        Long employeeId = userDetails.getEmployeeId();
        log.info("[WS] 메시지 수신: {}",message);
        ChatMessages chatMessage = chatWebSocketService.handleMessage(message, employeeId);
        log.info("[WS] 브로드캐스트: /sub/chat/room/ : {} - {}",message.getRoomId(), chatMessage);
        messagingTemplate.convertAndSend("/sub/chat/room/" + message.getRoomId(), chatMessage);

        // ★★★ 추가: 참여자 각각에게 "새 메시지 알림" push
        // message.getParticipantUserIds()는 List<Long> 또는 List<Integer> 형태여야 함
        for (Long participantId : message.getParticipantEmployeeIds()) {
            messagingTemplate.convertAndSend("/sub/chat/user/" + participantId, Map.of("roomId", message.getRoomId(),"senderId", employeeId ));
        }
    }

    @EventListener
    public void handleWebSocketConnectListener(SessionConnectedEvent event) {
        log.info("[WebSocket] 연결 성공: {}", event.getMessage().getHeaders().getTimestamp());
    }

}