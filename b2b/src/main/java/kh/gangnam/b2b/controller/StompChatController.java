package kh.gangnam.b2b.controller;

import kh.gangnam.b2b.dto.auth.CustomUserDetails;
import kh.gangnam.b2b.dto.chat.request.SendChat;
import kh.gangnam.b2b.dto.chat.response.ChatMessages;
import kh.gangnam.b2b.entity.auth.User;
import kh.gangnam.b2b.repository.UserRepository;
import kh.gangnam.b2b.service.ChatWebSocketService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

/**
 * WebSocket 채팅 메시지 송수신 컨트롤러
 * - 클라이언트가 /pub/chat/message로 메시지 발행 시 동작
 * - 처리 후 /sub/chat/room/{roomId}로 메시지 브로드캐스트
 */
@Controller
@RequiredArgsConstructor
public class StompChatController {
//브로커 추가해야함
    private final SimpMessagingTemplate messagingTemplate;
    private final ChatWebSocketService chatWebSocketService;
    private final UserRepository userRepository;

    @MessageMapping("/chat/messages")
    public void message(@Payload SendChat message, Authentication authentication) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Long userId = userDetails.getUserId();
        System.out.println("[WS] 메시지 수신: " + message);
        ChatMessages chatMessage = chatWebSocketService.handleMessage(message, userId);
        System.out.println("[WS] 브로드캐스트: /sub/chat/room/" + message.getRoomId() + " " + chatMessage);
        messagingTemplate.convertAndSend("/sub/chat/room/" + message.getRoomId(), chatMessage);

    }

}