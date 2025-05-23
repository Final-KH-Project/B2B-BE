package kh.gangnam.b2b.service.ServiceImpl;

import kh.gangnam.b2b.dto.chat.request.CreateRoom;
import kh.gangnam.b2b.dto.chat.request.SendChat;
import kh.gangnam.b2b.dto.chat.response.ChatMessages;
import kh.gangnam.b2b.dto.chat.response.ReadRoom;
import kh.gangnam.b2b.dto.chat.response.ReadRooms;
import kh.gangnam.b2b.entity.auth.Employee;
import kh.gangnam.b2b.entity.chat.ChatMessage;
import kh.gangnam.b2b.entity.chat.ChatRoom;
import kh.gangnam.b2b.entity.chat.ChatRoomEmployee;
import kh.gangnam.b2b.repository.ChatMessageRepository;
import kh.gangnam.b2b.repository.ChatRoomRepository;
import kh.gangnam.b2b.repository.ChatRoomEmployeeRepository;
import kh.gangnam.b2b.repository.EmployeeRepository;
import kh.gangnam.b2b.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 채팅 관련 REST API 서비스 구현체
 */
@Service
@RequiredArgsConstructor
@Transactional
public class ChatServiceImpl implements ChatService {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatRoomEmployeeRepository chatRoomEmployeeRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final EmployeeRepository employeeRepository;

    /**
     * 채팅방 생성
     *
     * @param createRoom 채팅방 생성 요청 DTO
     * @return 생성된 채팅방 ID
     */
    @Override
    public Long createRoom(CreateRoom createRoom) {
        List<Long> employeeIds = createRoom.getEmployeeIds();
        // 1. userIds 정렬 (1,2와 2,1을 동일하게 처리)
        List<Long> sortedEmployeeIds = employeeIds.stream().sorted().toList();

        // 2. 이미 같은 멤버 조합의 채팅방이 있는지 조회
        List<ChatRoom> candidateRooms = chatRoomEmployeeRepository.findChatRoomsByEmployeeId(sortedEmployeeIds.get(0));
        for (ChatRoom room : candidateRooms) {
            List<Long> participantIds = room.getChatRoomEmployees().stream()
                    .map(cru -> cru.getEmployee().getEmployeeId())
                    .sorted()
                    .toList();
            if (participantIds.equals(sortedEmployeeIds)) {
                // 이미 존재하는 방이면 그 roomId 반환
                return room.getId();
            }
        }

        // 3. 없으면 새로 생성
        ChatRoom room = new ChatRoom();
        room.setTitle(createRoom.getTitle());
        ChatRoom savedRoom = chatRoomRepository.save(room);

        List<ChatRoomEmployee> members = employeeIds.stream()
                .map(employeeId -> {
                    Employee employee = employeeRepository.findById(employeeId)
                            .orElseThrow(() -> new RuntimeException("User not found"));
                    ChatRoomEmployee cru = new ChatRoomEmployee();
                    cru.setEmployee(employee);
                    cru.setChatRoom(savedRoom);
                    return cru;
                }).collect(Collectors.toList());
        chatRoomEmployeeRepository.saveAll(members);

        return savedRoom.getId();
    }


    /**
     * 내 채팅방 목록 조회
     *
     * @param employeeId 사용자 ID
     * @return 채팅방 리스트 DTO
     */
    @Override
    public List<ReadRooms> readRooms(Long employeeId) {
        // 1. 내가 속한 채팅방 리스트 조회
        List<ChatRoom> rooms = chatRoomEmployeeRepository.findChatRoomsByEmployeeId(employeeId);

        // 2. 각 채팅방마다 최신 메시지/시간 포함해서 DTO로 변환
        return rooms.stream()
                .map(room -> {
                    // 최신 메시지 찾기 (메시지가 없을 수도 있으니 null 체크)
                    ChatMessage lastMsg = room.getMessages().stream()
                            .max(Comparator.comparing(ChatMessage::getSentAt))
                            .orElse(null);

                    String lastMessage = lastMsg != null ? lastMsg.getContent() : "";
                    LocalDateTime updatedAt = lastMsg != null ? lastMsg.getSentAt() : room.getCreatedAt();

                    return new ReadRooms(
                            room.getId(),
                            room.getTitle(),
                            room.getCreatedAt(),
                            lastMessage,
                            updatedAt
                    );
                })
                .sorted(Comparator.comparing(ReadRooms::getUpdatedAt).reversed()) // 최신순 정렬
                .collect(Collectors.toList());
    }

    /**
     * 채팅방 + 메시지 내역 조회
     *
     * @param roomId 채팅방 ID
     * @return 채팅방 상세 DTO (메시지 포함)
     */
    @Override
    public ReadRoom readRoom(Long roomId) {
        // 채팅방 + 메시지 한 번에 조회 (N+1 방지)
        ChatRoom room = chatRoomRepository.findChatRoomWithMessages(roomId)
                .orElseThrow(() -> new RuntimeException("채팅방을 찾을 수 없습니다."));
        // 메시지 엔티티 → DTO 변환
        List<ChatMessages> messages = room.getMessages().stream()
                .map(msg -> new ChatMessages(
                        msg.getId(),
                        msg.getChatRoom().getId(),
                        msg.getSender().getEmployeeId(),
                        msg.getContent(),
                        msg.getSentAt()
                )).collect(Collectors.toList());

        // 채팅방 + 메시지 내역 DTO 반환
        ReadRoom dto = new ReadRoom();
        dto.setRoomId(room.getId());
        dto.setTitle(room.getTitle());
        dto.setCreatedAt(room.getCreatedAt());
        dto.setMessages(messages);
        return dto;
    }

    /**
     * 메시지 저장 (REST API용)
     *
     * @param sendChat 메시지 전송 요청 DTO
     */
    @Override
    public void send(SendChat sendChat) {
        ChatRoom room;

        // 1. 채팅방이 없으면 새로 생성, 있으면 기존 채팅방 사용
        if (sendChat.getRoomId() == null) {
            // 방 제목, 참여자 등은 SendChat에 추가 정보가 필요할 수 있음
            room = new ChatRoom();
            room.setTitle(sendChat.getTitle() != null ? sendChat.getTitle() : "새 채팅방");
            room = chatRoomRepository.save(room);
        } else {
            room = chatRoomRepository.findById(sendChat.getRoomId())
                    .orElseThrow(() -> new RuntimeException("채팅방 없음"));
        }

        // 2. 중간 테이블(참여자) 저장 (없으면 추가)
        // SendChat에 참여자 리스트가 있다고 가정 (없으면 sender만 추가)
        List<Long> participantIds = sendChat.getParticipantEmployeeIds() != null
                ? sendChat.getParticipantEmployeeIds()
                : List.of(sendChat.getSenderId());

        for (Long userId : participantIds) {
            boolean exists = chatRoomEmployeeRepository.existsEmployeeInChatRoom(userId, room.getId());
            if (!exists) {
                Employee employee = employeeRepository.findById(userId)
                        .orElseThrow(() -> new RuntimeException("User not found"));
                ChatRoomEmployee cru = new ChatRoomEmployee();
                cru.setEmployee(employee);
                cru.setChatRoom(room);
                chatRoomEmployeeRepository.save(cru);
            }
        }

        // 3. 채팅 메시지 저장
        Employee sender = employeeRepository.findById(sendChat.getSenderId())
                .orElseThrow(() -> new RuntimeException("사용자 없음"));
        ChatMessage message = new ChatMessage();
        message.setChatRoom(room);
        message.setSender(sender);
        message.setContent(sendChat.getMessage());
        message.setSentAt(LocalDateTime.now());
        chatMessageRepository.save(message);
    }
}
