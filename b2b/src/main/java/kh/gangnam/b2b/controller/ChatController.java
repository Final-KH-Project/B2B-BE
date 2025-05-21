package kh.gangnam.b2b.controller;

import kh.gangnam.b2b.dto.auth.CustomUserDetails;
import kh.gangnam.b2b.dto.chat.request.CreateRoom;
import kh.gangnam.b2b.dto.chat.request.SendChat;
import kh.gangnam.b2b.dto.chat.response.ReadRoom;
import kh.gangnam.b2b.dto.chat.response.ReadRooms;
import kh.gangnam.b2b.entity.auth.User;
import kh.gangnam.b2b.repository.UserRepository;
import kh.gangnam.b2b.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 채팅 관련 REST API 컨트롤러
 * - 채팅방 생성, 내 채팅방 목록 조회, 채팅방 상세(메시지 내역) 조회, 메시지 전송 기능 제공
 */
@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatController {

    // 채팅 서비스 의존성 주입
    private final ChatService chatService;
    private final UserRepository userRepository;

    // 유저 목록 조회 API 추가
    @GetMapping("/user")
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // currentUser 조회
    @GetMapping("/me")
    public User getCurrentUser(@AuthenticationPrincipal CustomUserDetails userDetails) {
        return userRepository.findByUsername(userDetails.getUsername());
    }
    /**
     * 채팅방 생성 API
     * - 프론트에서 채팅방 생성 요청 시 사용
     * - 참여자(userIds), 방 이름(title) 등 CreateRoom DTO로 전달
     * @param createRoom 채팅방 생성 요청 DTO
     * @return 생성된 채팅방의 ID
     */
    @PostMapping("/rooms")
    public ResponseEntity<Long> createRoom(@RequestBody CreateRoom createRoom) {
        return ResponseEntity.ok(chatService.createRoom(createRoom));
    }

    /**
     * 내 채팅방 목록 조회 API
     * - userId로 내가 속한 채팅방 리스트를 반환
     * - 중간테이블(ChatRoomUser) 기반 조회
     * @param userId 사용자 ID
     * @return 채팅방 리스트 DTO
     */
    @GetMapping("/users/{userId}/rooms")
    public ResponseEntity<List<ReadRooms>> getMyRooms(@PathVariable("userId") Long userId) {
        return ResponseEntity.ok(chatService.readRooms(userId));
    }

    /**
     * 채팅방 상세(메시지 내역) 조회 API
     * - 채팅방 ID로 해당 채팅방과 메시지 내역을 한 번에 조회
     * - N+1 문제 없이 JOIN FETCH로 가져옴
     * @param roomId 채팅방 ID
     * @return 채팅방+메시지 내역 DTO
     */
    @GetMapping("/rooms/{roomId}")
    public ResponseEntity<ReadRoom> getRoomWithMessages(@PathVariable("roomId") Long roomId) {
        return ResponseEntity.ok(chatService.readRoom(roomId));
    }

    /**
     * 메시지 전송 API (REST 방식)
     * - 채팅방, 유저, 멤버십 검증 후 메시지 저장
     * - 실시간(WebSocket)과 별개로 REST 방식 메시지 저장이 필요할 때 사용
     * @param sendChat 메시지 전송 요청 DTO
     * @return 성공 시 200 OK 반환
     */
    @PostMapping("/messages")
    public ResponseEntity<Void> sendMessage(@RequestBody SendChat sendChat) {
        chatService.send(sendChat);
        return ResponseEntity.ok().build();
    }
}
