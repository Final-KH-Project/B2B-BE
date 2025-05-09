package kh.gangnam.b2b.service.ServiceImpl;

import kh.gangnam.b2b.dto.chat.request.CreateRoom;
import kh.gangnam.b2b.dto.chat.request.SendChat;
import kh.gangnam.b2b.dto.chat.response.ReadRoom;
import kh.gangnam.b2b.entity.chat.ChatRoomUser;
import kh.gangnam.b2b.service.ChatService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ChatServiceImpl implements ChatService {

    // Chat 서비스 비즈니스 로직 구현

    @Override
    public ResponseEntity<?> send(SendChat sendChat) {
        return null;
    }

    @Override
    public ResponseEntity<?> createRoom(CreateRoom createRoom) {
        return null;
    }

    @Override
    public ResponseEntity<List<ChatRoomUser>> readRooms(Long userId) {
        return null;
    }

    @Override
    public ResponseEntity<ReadRoom> readRoom(Long roomId) {
        return null;
    }
}
