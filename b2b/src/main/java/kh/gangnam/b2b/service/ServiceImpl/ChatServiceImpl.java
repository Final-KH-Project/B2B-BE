package kh.gangnam.b2b.service.ServiceImpl;

import kh.gangnam.b2b.dto.chat.request.CreateRoom;
import kh.gangnam.b2b.dto.chat.request.SendChat;
import kh.gangnam.b2b.dto.chat.response.ReadRoom;
import kh.gangnam.b2b.dto.chat.response.ReadRooms;
import kh.gangnam.b2b.entity.auth.User;
import kh.gangnam.b2b.entity.chat.ChatMessage;
import kh.gangnam.b2b.entity.chat.ChatRoom;
import kh.gangnam.b2b.entity.chat.ChatRoomUser;
import kh.gangnam.b2b.repository.ChatMessageRepository;
import kh.gangnam.b2b.repository.ChatRoomRepository;
import kh.gangnam.b2b.repository.ChatRoomUserRepository;
import kh.gangnam.b2b.repository.UserRepository;
import kh.gangnam.b2b.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatRoomUserRepository chatRoomUserRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final UserRepository userRepository;

    // ✅ 채팅방 생성
    @Override
    public ResponseEntity<?> createRoom(CreateRoom createRoom) {
        // 1. 채팅방 생성
        ChatRoom room = new ChatRoom();
        room.setTitle(createRoom.getTitle());  // 채팅방 제목 설정
        ChatRoom savedRoom = chatRoomRepository.save(room);  // DB에 저장

        // 2. 참여자 목록 저장
        List<ChatRoomUser> members = createRoom.getUserIds().stream()
                .map(userId -> {
                    ChatRoomUser cru = new ChatRoomUser();
                    cru.setUserId(userId);
                    cru.setRoom(savedRoom);
                    return cru;
                }).collect(Collectors.toList());

        chatRoomUserRepository.saveAll(members);  // DB에 참여자 저장

        return ResponseEntity.ok(savedRoom.getId());  // 채팅방 생성 성공, 채팅방 ID 반환
    }

    // ✅ 메시지 전송
    @Override
    public ResponseEntity<?> send(SendChat sendChat) {
        // 1. 채팅방 존재 확인
        ChatRoom room = chatRoomRepository.findById(sendChat.getRoomId())
                .orElseThrow(() -> new RuntimeException("채팅방이 존재하지 않습니다."));

        // 2. 참여자 검증 (보안) - 메시지를 보낸 사용자가 해당 채팅방의 참여자인지 확인
        boolean isMember = chatRoomUserRepository.existsByUserIdAndRoom(sendChat.getSenderId(), sendChat.getRoomId());
        if (!isMember) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("채팅방 참여자가 아닙니다.");
        }

        // 3. 메시지 저장
        ChatMessage message = new ChatMessage();
        message.setRoomId(sendChat.getRoomId());
        message.setSenderId(sendChat.getSenderId());
        message.setMessage(sendChat.getMessage());
        message.setSentAt(LocalDateTime.now());  // 메시지 전송 시간 설정
        chatMessageRepository.save(message);  // DB에 메시지 저장

        // 4. 실시간 메시지 전송 (옵션) - WebSocket 등을 활용한 실시간 알림 등 (이 부분은 선택적 구현)

        // 5. 응답 반환
        return ResponseEntity.ok("메시지 전송 성공");
    }

    // ✅ 내 채팅방 목록 조회
    @Override
    public ResponseEntity<List<ReadRooms>> readRooms(Long userId) {
        // 1. 사용자가 참여한 채팅방 목록 조회
        List<ChatRoomUser> userRooms = chatRoomUserRepository.findByUserIdOrderByIdDesc(userId);

        // 2. 채팅방 DTO로 변환
        List<ReadRooms> rooms = userRooms.stream()
                .map(roomUser -> {
                    ReadRooms readRoom = new ReadRooms();
                    readRoom.setRoomId(roomUser.getRoom().getId());
                    readRoom.setTitle(roomUser.getRoom().getTitle());
                    readRoom.setCreatedAt(roomUser.getRoom().getCreatedAt());
                    return readRoom;
                }).collect(Collectors.toList());

        return ResponseEntity.ok(rooms);
    }

    // ✅ 채팅방 입장 (내역 조회)
    @Override
    public ResponseEntity<ReadRoom> readRoom(Long roomId) {
        // 1. 채팅방 조회
        ChatRoom room = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new RuntimeException("채팅방을 찾을 수 없습니다."));

        // 2. 채팅방에 속한 메시지 조회
        List<ChatMessage> messages = chatMessageRepository.findByRoomIdOrderBySentAtAsc(roomId);

        // 3. DTO 생성 및 반환
        ReadRoom response = new ReadRoom();
        response.setRoomId(room.getId());
        response.setTitle(room.getTitle());
        response.setCreatedAt(room.getCreatedAt());
        response.setMessages(messages);  // 메시지 목록을 DTO에 추가

        return ResponseEntity.ok(response);
    }


    @Override
    public ResponseEntity<ChatMessage> handleMessage(SendChat message, Long userId) {
        // 1. 사용자 조회 (username → userId)
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        // 2. 메시지 엔티티 생성 및 저장
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setRoomId(message.getRoomId());
        chatMessage.setSenderId(user.getId());
        chatMessage.setMessage(message.getMessage());
        chatMessage.setSentAt(LocalDateTime.now());
        ChatMessage savedMessage = chatMessageRepository.save(chatMessage);

        // 3. 응답 반환
        return ResponseEntity.ok(savedMessage);
    }

}
