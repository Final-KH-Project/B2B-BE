package kh.gangnam.b2b.service.ServiceImpl;

import kh.gangnam.b2b.dto.chat.request.SendChat;
import kh.gangnam.b2b.dto.chat.response.ChatMessages;
import kh.gangnam.b2b.entity.auth.Employee;
import kh.gangnam.b2b.entity.chat.ChatMessage;
import kh.gangnam.b2b.entity.chat.ChatRoom;
import kh.gangnam.b2b.exception.NotFoundException;
import kh.gangnam.b2b.repository.ChatMessageRepository;
import kh.gangnam.b2b.repository.ChatRoomRepository;
import kh.gangnam.b2b.repository.ChatRoomEmployeeRepository;
import kh.gangnam.b2b.repository.EmployeeRepository;
import kh.gangnam.b2b.service.ChatWebSocketService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * WebSocket 실시간 메시지 저장 및 응답 DTO 변환 서비스 구현체
 */
@Service
@RequiredArgsConstructor
@Transactional
public class ChatWebSocketServiceImpl implements ChatWebSocketService {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final EmployeeRepository employeeRepository;
    private final ChatRoomEmployeeRepository chatRoomEmployeeRepository;

    @Override
    public ChatMessages handleMessage(SendChat message, Long employeeId) {
        // 1. 채팅방, 유저, 멤버십 검증
        ChatRoom room = chatRoomRepository.findById(message.getRoomId())
                .orElseThrow(() -> new NotFoundException("채팅방 없음"));
        Employee sender = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new NotFoundException("사용자 없음"));
        boolean isMember = chatRoomEmployeeRepository.existsEmployeeInChatRoom(employeeId, room.getId());
        if (!isMember) throw new NotFoundException("채팅방 참여자가 아닙니다.");

        // 2. 메시지 저장
        ChatMessage entity = new ChatMessage();
        entity.setChatRoom(room);
        entity.setSender(sender);
        entity.setContent(message.getMessage());
        entity.setSentAt(LocalDateTime.now());
        ChatMessage saved = chatMessageRepository.save(entity);

        // 3. 엔티티 → DTO 변환 후 반환
        return new ChatMessages(
                saved.getId(),
                saved.getChatRoom().getId(),
                saved.getSender().getEmployeeId(),
                saved.getContent(),
                saved.getSentAt()
        );
    }
}
