package kh.gangnam.b2b.service;

import jakarta.persistence.Entity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import kh.gangnam.b2b.dto.chat.request.CreateRoom;
import kh.gangnam.b2b.dto.chat.request.SendChat;
import kh.gangnam.b2b.dto.chat.response.ChatEmployee;
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
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ChatServiceImpl implements ChatService {
    private final ChatRoomRepository chatRoomRepository;
    private final ChatRoomEmployeeRepository chatRoomEmployeeRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final EmployeeRepository employeeRepository;

    @PersistenceContext
    private EntityManager em;
    // ==================== 직원(유저) 관련 ====================

    @Override
    public List<ChatEmployee> getAllEmployees() {
        // fetch join으로 부서 정보까지 한 번에 조회
        List<Employee> employees = em.createQuery(
                "SELECT e FROM Employee e LEFT JOIN FETCH e.dept", Employee.class
        ).getResultList();

        // 반드시 위에서 조회한 employees 리스트를 사용해야 함!
        return employees.stream()
                .map(ChatEmployee::fromEntity)
                .collect(Collectors.toList());
    }


    @Override
    public ChatEmployee getCurrentEmployee(String loginId) {
        Employee emp = employeeRepository.findByLoginId(loginId)
                .orElseThrow(() -> new RuntimeException("직원 정보를 찾을 수 없습니다."));
        // fromEntity 사용
        return ChatEmployee.fromEntity(emp);
    }

    // ==================== 채팅방 관련 ====================

    @Override
    public Long createRoom(CreateRoom createRoom) {
        List<Long> employeeIds = createRoom.getEmployeeIds();
        List<Long> sortedEmployeeIds = employeeIds.stream().sorted().toList();
        List<ChatRoom> candidateRooms = chatRoomEmployeeRepository.findAllRoomsByEmployeeIds(sortedEmployeeIds, sortedEmployeeIds.size());
        for (ChatRoom room : candidateRooms) {
            List<Long> participantIds = room.getChatRoomEmployees().stream()
                    .map(cru -> cru.getEmployee().getEmployeeId())
                    .sorted()
                    .toList();
            if (participantIds.equals(sortedEmployeeIds)) {
                for (ChatRoomEmployee cru : room.getChatRoomEmployees()) {
                    if (employeeIds.contains(cru.getEmployee().getEmployeeId()) && Boolean.FALSE.equals(cru.getActive())) {
                        cru.setActive(true);
                        chatRoomEmployeeRepository.save(cru);
                    }
                }
                return room.getId();
            }
        }
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
                    cru.setActive(true);
                    cru.setLastExitAt(null);
                    return cru;
                }).collect(Collectors.toList());
        chatRoomEmployeeRepository.saveAll(members);
        return savedRoom.getId();
    }

    @Override
    public List<ReadRooms> readRooms(Long employeeId) {
        List<ChatRoom> rooms = chatRoomEmployeeRepository.findChatRoomsByEmployeeIds(employeeId);
        return rooms.stream()
                .map(room -> {
                    ChatMessage lastMsg = room.getMessages().stream()
                            .max(Comparator.comparing(ChatMessage::getSentAt))
                            .orElse(null);
                    String lastMessage = lastMsg != null ? lastMsg.getContent() : "";
                    LocalDateTime updatedAt = lastMsg != null ? lastMsg.getSentAt() : room.getCreatedAt();
                    ChatRoomEmployee cru = room.getChatRoomEmployees().stream()
                            .filter(e -> e.getEmployee().getEmployeeId().equals(employeeId))
                            .findFirst()
                            .orElse(null);
                    Boolean active = cru != null ? cru.getActive() : null;
                    return new ReadRooms(
                            room.getId(),
                            room.getTitle(),
                            room.getCreatedAt(),
                            lastMessage,
                            updatedAt,
                            Boolean.TRUE.equals(active)
                    );
                })
                .sorted(Comparator.comparing(ReadRooms::getUpdatedAt).reversed())
                .collect(Collectors.toList());
    }

    @Override
    public ReadRoom readRoom(Long roomId, Long employeeId) {
        ChatRoom room = chatRoomRepository.findChatRoomWithMessages(roomId)
                .orElseThrow(() -> new RuntimeException("채팅방을 찾을 수 없습니다."));
        ChatRoomEmployee cru = chatRoomEmployeeRepository
                .findByChatRoom_IdAndEmployee_EmployeeId(roomId, employeeId)
                .orElseThrow(() -> new RuntimeException("참여자 정보를 찾을 수 없습니다."));
        LocalDateTime lastExitAt = cru.getLastExitAt();
        List<ChatMessages> messages = room.getMessages().stream()
                .filter(msg -> lastExitAt == null || msg.getSentAt().isAfter(lastExitAt))
                .map(msg -> new ChatMessages(
                        msg.getId(),
                        msg.getChatRoom().getId(),
                        msg.getSender().getEmployeeId(),
                        msg.getContent(),
                        msg.getSentAt()
                )).collect(Collectors.toList());
        ReadRoom dto = new ReadRoom();
        dto.setRoomId(room.getId());
        dto.setTitle(room.getTitle());
        dto.setCreatedAt(room.getCreatedAt());
        dto.setMessages(messages);
        return dto;
    }

    @Override
    public void leaveRoom(Long chatRoomId, Long employeeId) {
        Optional<ChatRoomEmployee> opt = chatRoomEmployeeRepository
                .findByChatRoom_IdAndEmployee_EmployeeId(chatRoomId, employeeId);
        if (opt.isEmpty()) {
            return;
        }
        ChatRoomEmployee cru = opt.get();
        cru.setActive(false); // 비활성화(soft delete)
        cru.setLastExitAt(LocalDateTime.now()); // 마지막 퇴장 시각 기록
        chatRoomEmployeeRepository.save(cru);
        int memberCount = chatRoomEmployeeRepository.countByChatRoom_IdAndActive(chatRoomId, true);
        if (memberCount == 0) {
            chatMessageRepository.deleteByChatRoom_Id(chatRoomId);
            chatRoomRepository.deleteById(chatRoomId);
        }
    }

    // ==================== 메시지 관련 ====================

    @Override
    public void send(SendChat sendChat) {
        ChatRoom room;
        if (sendChat.getRoomId() == null) {
            room = new ChatRoom();
            room.setTitle(sendChat.getTitle() != null ? sendChat.getTitle() : "새 채팅방");
            room = chatRoomRepository.save(room);
        } else {
            room = chatRoomRepository.findById(sendChat.getRoomId())
                    .orElseThrow(() -> new RuntimeException("채팅방이 없습니다."));
        }
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
        Employee sender = employeeRepository.findById(sendChat.getSenderId())
                .orElseThrow(() -> new RuntimeException("사용자가 없습니다."));
        ChatMessage message = new ChatMessage();
        message.setChatRoom(room);
        message.setSender(sender);
        message.setContent(sendChat.getMessage());
        message.setSentAt(LocalDateTime.now());
        chatMessageRepository.save(message);
    }

    @Override
    public void markAsRead(Long roomId, Long employeeId, Long lastReadMessageId) {
        ChatRoomEmployee cru = chatRoomEmployeeRepository
                .findByChatRoom_IdAndEmployee_EmployeeId(roomId, employeeId)
                .orElseThrow(() -> new RuntimeException("채팅방-참여자 정보가 없습니다."));
        cru.setLastReadMessageId(lastReadMessageId);
        cru.setLastReadAt(LocalDateTime.now());
        chatRoomEmployeeRepository.save(cru);
    }

    @Override
    public int getUnreadCount(Long roomId, Long employeeId) {
        ChatRoomEmployee cru = chatRoomEmployeeRepository
                .findByChatRoom_IdAndEmployee_EmployeeId(roomId, employeeId)
                .orElseThrow(() -> new RuntimeException("채팅방-참여자 정보가 없습니다."));
        Long lastReadId = cru.getLastReadMessageId() != null ? cru.getLastReadMessageId() : 0L;
        return chatMessageRepository.countUnreadMessages(roomId, lastReadId);
    }

    @Override
    public Map<String, Integer> getAllUnreadCountsAsStringKey(Long employeeId) {
        Map<String, Integer> result = new HashMap<>();
        List<ChatRoomEmployee> participants = chatRoomEmployeeRepository.findByEmployee_EmployeeId(employeeId);
        if (participants == null || participants.isEmpty()) {
            return result;
        }
        for (ChatRoomEmployee p : participants) {
            if (p.getChatRoom() == null) continue;
            Long roomId = p.getChatRoom().getId();
            if (roomId == null) continue;
            Long lastReadId = p.getLastReadMessageId() == null ? 0L : p.getLastReadMessageId();
            int count = chatMessageRepository.countUnreadMessages(roomId, lastReadId);
            result.put(String.valueOf(roomId), count);
        }
        return result;
    }
}
