package kh.gangnam.b2b.service.ServiceImpl;

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
import kh.gangnam.b2b.exception.InvalidRequestException;
import kh.gangnam.b2b.exception.NotFoundException;
import kh.gangnam.b2b.repository.ChatMessageRepository;
import kh.gangnam.b2b.repository.ChatRoomRepository;
import kh.gangnam.b2b.repository.ChatRoomEmployeeRepository;
import kh.gangnam.b2b.repository.EmployeeRepository;
import kh.gangnam.b2b.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
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
    @PersistenceContext private EntityManager em;

    private Employee getEmp(Long id) {
        return employeeRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("사원 정보를 찾을 수 없습니다."));
    }

    private ChatRoom getRoom(Long id) {
        return chatRoomRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("채팅방이 없습니다."));
    }

    private ChatRoomEmployee getCru(Long roomId, Long empId) {
        return chatRoomEmployeeRepository.findByChatRoom_IdAndEmployee_EmployeeId(roomId, empId)
                .orElseThrow(() -> new NotFoundException("참여자 정보가 없습니다."));
    }

    // 입력값 검증 유틸리티
    private void validateNotEmpty(Object value, String msg) {
        if (value == null ||
                (value instanceof String s && s.trim().isEmpty()) ||
                (value instanceof List<?> l && l.isEmpty())) {
            throw new InvalidRequestException(msg);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<ChatEmployee> getAllEmployees() {
        return em.createQuery("SELECT e FROM Employee e LEFT JOIN FETCH e.dept", Employee.class)
                .getResultList().stream()
                .map(ChatEmployee::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public ChatEmployee getCurrentEmployee(String loginId) {
        return ChatEmployee.fromEntity(employeeRepository.findByLoginId(loginId)
                .orElseThrow(() -> new NotFoundException("직원 정보를 찾을 수 없습니다.")));
    }

    @Override
    public Long createRoom(CreateRoom req) {
        validateNotEmpty(req, "요청이 비어 있습니다.");
        validateNotEmpty(req.getEmployeeIds(), "참여자 목록이 비어 있습니다.");
        validateNotEmpty(req.getTitle(), "채팅방 제목이 비어 있습니다.");

        List<Long> ids = req.getEmployeeIds();
        List<Long> sorted = ids.stream().sorted().toList();

        Optional<ChatRoom> existingRoom = chatRoomEmployeeRepository
                .findAllRoomsByEmployeeIds(sorted, sorted.size())
                .stream()
                .filter(r -> r.getChatRoomEmployees().stream()
                        .map(c -> c.getEmployee().getEmployeeId())
                        .sorted()
                        .toList()
                        .equals(sorted))
                .findFirst();

        if (existingRoom.isPresent()) {
            existingRoom.get().getChatRoomEmployees().forEach(c -> {
                if (ids.contains(c.getEmployee().getEmployeeId()) && Boolean.FALSE.equals(c.getActive())) {
                    c.setActive(true);
                    chatRoomEmployeeRepository.save(c);
                }
            });
            return existingRoom.get().getId();
        }

        ChatRoom room = new ChatRoom();
        room.setTitle(req.getTitle());
        chatRoomRepository.save(room);

        List<ChatRoomEmployee> employees = ids.stream()
                .map(id -> {
                    ChatRoomEmployee cru = new ChatRoomEmployee();
                    cru.setEmployee(getEmp(id));
                    cru.setChatRoom(room);
                    cru.setActive(true);
                    cru.setLastExitAt(null);
                    return cru;
                })
                .toList();
        chatRoomEmployeeRepository.saveAll(employees);

        return room.getId();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReadRooms> readRooms(Long empId) {
        return chatRoomEmployeeRepository.findChatRoomsByEmployeeIds(empId).stream()
                .map(room -> {
                    ChatMessage lastMsg = room.getMessages().stream()
                            .max(Comparator.comparing(ChatMessage::getSentAt))
                            .orElse(null);
                    LocalDateTime updatedAt = lastMsg != null ? lastMsg.getSentAt() : room.getCreatedAt();
                    ChatRoomEmployee cru = room.getChatRoomEmployees().stream()
                            .filter(e -> e.getEmployee().getEmployeeId().equals(empId))
                            .findFirst()
                            .orElse(null);
                    boolean isActive = cru != null && Boolean.TRUE.equals(cru.getActive());
                    return new ReadRooms(room.getId(), room.getTitle(), room.getCreatedAt(),
                            lastMsg != null ? lastMsg.getContent() : "", updatedAt, isActive);
                })
                .sorted(Comparator.comparing(ReadRooms::getUpdatedAt).reversed())
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public ReadRoom readRoom(Long roomId, Long empId) {
        ChatRoom room = chatRoomRepository.findChatRoomWithMessages(roomId)
                .orElseThrow(() -> new NotFoundException("채팅방을 찾을 수 없습니다."));
        ChatRoomEmployee cru = getCru(roomId, empId);
        LocalDateTime lastExitAt = cru.getLastExitAt();
        List<ChatMessages> msgs = room.getMessages().stream()
                .filter(m -> lastExitAt == null || m.getSentAt().isAfter(lastExitAt))
                .map(m -> new ChatMessages(m.getId(), m.getChatRoom().getId(),
                        m.getSender().getEmployeeId(), m.getContent(), m.getSentAt()))
                .toList();
        return new ReadRoom(room.getId(), room.getTitle(), room.getCreatedAt(), msgs);
    }

    @Override
    public void leaveRoom(Long roomId, Long empId) {
        chatRoomEmployeeRepository.findByChatRoom_IdAndEmployee_EmployeeId(roomId, empId).ifPresent(cru -> {
            cru.setActive(false);
            cru.setLastExitAt(LocalDateTime.now());
            chatRoomEmployeeRepository.save(cru);
            if (chatRoomEmployeeRepository.countByChatRoom_IdAndActive(roomId, true) == 0) {
                chatMessageRepository.deleteByChatRoom_Id(roomId);
                chatRoomRepository.deleteById(roomId);
            }
        });
    }

    @Override
    public void send(SendChat req) {
        validateNotEmpty(req, "요청이 비어 있습니다.");
        validateNotEmpty(req.getSenderId(), "보내는 사람 정보가 없습니다.");
        validateNotEmpty(req.getMessage(), "메시지 내용이 없습니다.");

        ChatRoom room;
        if (req.getRoomId() == null) {
            room = new ChatRoom();
            room.setTitle(req.getTitle() != null ? req.getTitle() : "새 채팅방");
            chatRoomRepository.save(room);
        } else {
            room = getRoom(req.getRoomId());
        }

        if (!chatRoomEmployeeRepository.existsEmployeeInChatRoom(req.getSenderId(), room.getId())) {
            throw new InvalidRequestException("채팅방 참여자가 아닙니다.");
        }

        List<Long> participants = req.getParticipantEmployeeIds() != null
                ? req.getParticipantEmployeeIds()
                : List.of(req.getSenderId());

        participants.stream()
                .filter(id -> !chatRoomEmployeeRepository.existsEmployeeInChatRoom(id, room.getId()))
                .forEach(id -> {
                    ChatRoomEmployee cru = new ChatRoomEmployee();
                    cru.setEmployee(getEmp(id));
                    cru.setChatRoom(room);
                    cru.setActive(true);
                    cru.setLastExitAt(null);
                    chatRoomEmployeeRepository.save(cru);
                });

        ChatMessage message = new ChatMessage();
        message.setChatRoom(room);
        message.setSender(getEmp(req.getSenderId()));
        message.setContent(req.getMessage());
        message.setSentAt(LocalDateTime.now());
        chatMessageRepository.save(message);
    }

    @Override
    public void markAsRead(Long roomId, Long empId, Long lastReadMessageId) {
        ChatRoomEmployee cru = getCru(roomId, empId);
        cru.setLastReadMessageId(lastReadMessageId);
        cru.setLastReadAt(LocalDateTime.now());
        chatRoomEmployeeRepository.save(cru);
    }

    @Override
    @Transactional(readOnly = true)
    public int getUnreadCount(Long roomId, Long empId) {
        Long lastReadId = Optional.ofNullable(getCru(roomId, empId).getLastReadMessageId()).orElse(0L);
        return chatMessageRepository.countUnreadMessages(roomId, lastReadId);
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Integer> getAllUnreadCountsAsStringKey(Long empId) {
        Map<String, Integer> result = new HashMap<>();
        chatRoomEmployeeRepository.findByEmployee_EmployeeId(empId).stream()
                .filter(p -> p.getChatRoom() != null && p.getChatRoom().getId() != null)
                .forEach(p -> result.put(String.valueOf(p.getChatRoom().getId()),
                        chatMessageRepository.countUnreadMessages(
                                p.getChatRoom().getId(),
                                Optional.ofNullable(p.getLastReadMessageId()).orElse(0L)
                        )));
        return result;
    }
}
