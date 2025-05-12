package kh.gangnam.b2b.controller;

import kh.gangnam.b2b.dto.chat.request.SendChat;
import kh.gangnam.b2b.entity.chat.ChatMessage;
import kh.gangnam.b2b.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
public class StompChatController {
    private final SimpMessagingTemplate messagingTemplate;
    private final ChatService chatService;

    // StompChatController.java
    @MessageMapping("/chat/message")
    public void message(SendChat message, Principal principal) {
        // principal에서 사용자 이름 추출 (JWT 기반)
        Long userId = Long.valueOf(principal.getName());
        ResponseEntity<ChatMessage> chatMessage = chatService.handleMessage(message, userId);
        messagingTemplate.convertAndSend("/sub/chat/room/" + message.getRoomId(), chatMessage);
    }

}
