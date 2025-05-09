package kh.gangnam.b2b.service.ServiceImpl;

import kh.gangnam.b2b.dto.chat.request.CreateRoom;
import kh.gangnam.b2b.dto.chat.request.SendChat;
import kh.gangnam.b2b.dto.chat.response.ReadRoom;
import kh.gangnam.b2b.entity.chat.ChatRoom;
import kh.gangnam.b2b.entity.chat.ChatRoomUser;
import kh.gangnam.b2b.repository.ChatRoomRepository;
import kh.gangnam.b2b.repository.ChatRoomUserRepository;
import kh.gangnam.b2b.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;

import java.lang.reflect.Member;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatRoomUserRepository chatRoomUserRepository;
    // Chat 서비스 비즈니스 로직 구현

    @Override
    public ResponseEntity<?> send(SendChat sendChat) {
        return ResponseEntity.ok("메시지 전송 미구현");
    }

    @Override
    public ResponseEntity<?> createRoom(CreateRoom createRoom) {
        // 1. 채팅방 생성
        ChatRoom room = new ChatRoom();
        room.setTitle(createRoom.getTitle());
        // createdAt은 기본값(LocalDateTime.now())으로 자동 세팅됨
        ChatRoom savedRoom = chatRoomRepository.save(room);

        // 2. 참여자 목록 저장
        List<ChatRoomUser> members = createRoom.getUserIds().stream()
                .map(userId -> {
                    ChatRoomUser cru = new ChatRoomUser();
                    cru.setUserId(userId);
                    cru.setRoom(savedRoom);
                    return cru;
                })
                .toList();

        chatRoomUserRepository.saveAll(members);

        return ResponseEntity.ok(savedRoom.getId());
    }

    @Override
    public ResponseEntity<List<ChatRoomUser>> readRooms(Long userId) {
        return null;
    }
    //채팅방 리스트 조회 로직

    @Override
    public ResponseEntity<ReadRoom> readRoom(Long roomId) {
        return null;
    }
    //채팅방 입장 내역 조회 로직

}
