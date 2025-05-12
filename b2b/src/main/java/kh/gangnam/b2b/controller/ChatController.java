package kh.gangnam.b2b.controller;

import kh.gangnam.b2b.dto.chat.request.CreateRoom;
import kh.gangnam.b2b.dto.chat.request.SendChat;
import kh.gangnam.b2b.dto.chat.response.ReadRoom;
import kh.gangnam.b2b.dto.chat.response.ReadRooms;
import kh.gangnam.b2b.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/chat")
public class ChatController {

    private final ChatService chatService;



    // ✅ 채팅방 생성
    @PostMapping("/room")
    public ResponseEntity<?> createRoom(@RequestBody CreateRoom request) {
        return chatService.createRoom(request);
    }

    // ✅ 메시지 전송
    @PostMapping("/message")
    public ResponseEntity<?> sendMessage(@RequestBody SendChat request) {
        return chatService.send(request);
    }

    // ✅ 내 채팅방 목록 조회 (엔티티 → DTO 변환)
    @GetMapping("/users/{userId}/rooms")
    public ResponseEntity<List<ReadRooms>> getMyRooms(@PathVariable Long userId) {
        return chatService.readRooms(userId);
    }

    // ✅ 채팅방 입장 (채팅방 상세 내역 조회)
    @GetMapping("/rooms/{roomId}")
    public ResponseEntity<ReadRoom> enterRoom(@PathVariable Long roomId) {
        return chatService.readRoom(roomId);
    }
}
