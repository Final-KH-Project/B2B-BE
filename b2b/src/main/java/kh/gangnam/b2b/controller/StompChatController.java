package kh.gangnam.b2b.controller;

import kh.gangnam.b2b.dto.auth.CustomUserDetails;
import kh.gangnam.b2b.dto.chat.request.SendChat;
import kh.gangnam.b2b.dto.chat.response.ChatMessages;
import kh.gangnam.b2b.service.ChatWebSocketService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;

/**
 * WebSocket 채팅 메시지 송수신 컨트롤러
 * - 클라이언트가 /pub/chat/message로 메시지 발행 시 동작
 * - 처리 후 /sub/chat/room/{roomId}로 메시지 브로드캐스트
 */
@Controller
@RequiredArgsConstructor
public class StompChatController {

    private final SimpMessagingTemplate messagingTemplate;
    private final ChatWebSocketService chatWebSocketService;

    @MessageMapping("/chat/message")
    public void message(SendChat message, @AuthenticationPrincipal CustomUserDetails userDetails) {
        // 1. 메시지 저장 및 DTO 변환
        String userId = userDetails.getUserId();
        ChatMessages chatMessage = chatWebSocketService.handleMessage(message, userId);
        // 2. 해당 채팅방 구독자에게 메시지 브로드캐스트
        messagingTemplate.convertAndSend("/sub/chat/room/" + message.getRoomId(), chatMessage);
    }