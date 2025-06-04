package kh.gangnam.b2b.controller;

import kh.gangnam.b2b.config.security.CustomEmployeeDetails;
import kh.gangnam.b2b.dto.chat.request.*;
import kh.gangnam.b2b.dto.chat.response.*;
import kh.gangnam.b2b.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 채팅 관련 REST API 컨트롤러
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/chat")
public class ChatController {

    private final ChatService chatService;

    // ==================== 직원(유저) 관련 ====================

    /** 전체 직원 목록 조회 */
    @GetMapping("/user")
    public ResponseEntity<List<ChatEmployee>> getAllEmployees() {
        return ResponseEntity.ok(chatService.getAllEmployees());
    }

    /** 현재 로그인 직원 정보 조회 */
    @GetMapping("/me")
    public ResponseEntity<ChatEmployee> getCurrentEmployee(@AuthenticationPrincipal CustomEmployeeDetails user) {
        return ResponseEntity.ok(chatService.getCurrentEmployee(user.getUsername()));
    }

    // ==================== 채팅방 관련 ====================

    /** 채팅방 생성 */
    @PostMapping("/rooms")
    public ResponseEntity<Long> createRoom(@RequestBody CreateRoom req) {
        return ResponseEntity.ok(chatService.createRoom(req));
    }

    /** 내 채팅방 목록 조회 */
    @GetMapping("/users/{userId}/rooms")
    public ResponseEntity<List<ReadRooms>> getMyRooms(@PathVariable("userId") Long userId) {
        return ResponseEntity.ok(chatService.readRooms(userId));
    }

    /** 채팅방 상세(메시지 내역) 조회 */
    @GetMapping("/rooms/{roomId}/employees/{employeeId}")
    public ResponseEntity<ReadRoom> getRoomWithMessages(
            @PathVariable("roomId") Long roomId,
            @PathVariable("employeeId") Long employeeId) {
        return ResponseEntity.ok(chatService.readRoom(roomId, employeeId));
    }

    /** 채팅방 나가기 */
    @DeleteMapping("/rooms/{roomId}/employees/{employeeId}")
    public ResponseEntity<Void> leaveRoom(
            @PathVariable("roomId") Long roomId,
            @PathVariable("employeeId") Long employeeId) {
        chatService.leaveRoom(roomId, employeeId);
        return ResponseEntity.noContent().build();
    }

    // ==================== 메시지 관련 ====================

    /** 메시지 전송 */
    @PostMapping("/messages")
    public ResponseEntity<Void> sendMessage(@RequestBody SendChat req) {
        chatService.send(req);
        return ResponseEntity.ok().build();
    }

    /** 안읽은 메시지 처리 */
    @PostMapping("/rooms/{roomId}/read")
    public ResponseEntity<Void> markAsRead(
            @PathVariable("roomId") Long roomId,
            @RequestBody MarkAsReadRequest req) {
        chatService.markAsRead(roomId, req.getEmployeeId(), req.getLastReadMessageId());
        return ResponseEntity.ok().build();
    }

    /** 특정 채팅방의 안읽은 메시지 개수 */
    @GetMapping("/rooms/{roomId}/unread-count")
    public ResponseEntity<Integer> getUnreadCount(
            @PathVariable("roomId") Long roomId,
            @RequestParam("employeeId") Long employeeId) { // 이름 명시
        return ResponseEntity.ok(chatService.getUnreadCount(roomId, employeeId));
    }

    /** 내가 속한 채팅방의 전체 안읽은 메시지 개수*/
    @GetMapping("/rooms/unread-counts")
    public ResponseEntity<Map<String, Integer>> getAllUnreadCounts(@RequestParam("employeeId") Long employeeId) { // 이름 명시
        return ResponseEntity.ok(chatService.getAllUnreadCountsAsStringKey(employeeId));
    }

}
