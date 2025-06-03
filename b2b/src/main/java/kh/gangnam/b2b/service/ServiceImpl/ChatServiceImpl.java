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

    /**
     * 채팅방을 생성하거나, 기존 동일 참여자 방이 있으면 재사용하여 방 ID를 반환한다.
     * @param createRoom 채팅방 생성 요청 DTO (참여자 목록, 방 제목 등)
     * @return 생성된(또는 기존) 채팅방의 ID
     */
    @Override
    public Long createRoom(CreateRoom createRoom) {
        List<Long> employeeIds = createRoom.getEmployeeIds();
        List<Long> sortedEmployeeIds = employeeIds.stream().sorted().toList();

        // 참여자 목록이 동일한 기존 방이 있는지 조회 (모두 active 상태가 아닐 수도 있음)
        List<ChatRoom> candidateRooms = chatRoomEmployeeRepository.findAllRoomsByEmployeeIds(sortedEmployeeIds, sortedEmployeeIds.size());

        for (ChatRoom room : candidateRooms) {
            // 방의 참여자 ID 목록을 정렬하여 비교
            List<Long> participantIds = room.getChatRoomEmployees().stream()
                    .map(cru -> cru.getEmployee().getEmployeeId())
                    .sorted()
                    .toList();
            if (participantIds.equals(sortedEmployeeIds)) {
                // 기존 방의 참여자 중 비활성(active=false)이면 활성화 처리
                for (ChatRoomEmployee cru : room.getChatRoomEmployees()) {
                    if (employeeIds.contains(cru.getEmployee().getEmployeeId()) && Boolean.FALSE.equals(cru.getActive())) {
                        cru.setActive(true);
                        chatRoomEmployeeRepository.save(cru);
                    }
                }
                // 기존 방 ID 반환
                return room.getId();
            }
        }

        // 동일한 방이 없으면 새 채팅방 생성
        ChatRoom room = new ChatRoom();
        room.setTitle(createRoom.getTitle());
        ChatRoom savedRoom = chatRoomRepository.save(room);

        // 참여자별 ChatRoomEmployee 엔티티 생성 및 저장
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

    /**
     * 사용자가 채팅방을 나갈 때(soft delete) 처리한다.
     * @param chatRoomId 채팅방 ID
     * @param employeeId 나가는 사용자 ID
     */
    @Override
    public void leaveRoom(Long chatRoomId, Long employeeId) {
        Optional<ChatRoomEmployee> opt = chatRoomEmployeeRepository
                .findByChatRoom_IdAndEmployee_EmployeeId(chatRoomId, employeeId);
        if (opt.isEmpty()) {
            // 참여 정보가 없으면 아무 처리도 하지 않음
            return;
        }
        ChatRoomEmployee cru = opt.get();
        cru.setActive(false); // 비활성화(soft delete)
        cru.setLastExitAt(LocalDateTime.now()); // 마지막 퇴장 시각 기록
        chatRoomEmployeeRepository.save(cru);

        // 방에 남은 active 멤버가 없으면 메시지와 방 자체를 삭제
        int memberCount = chatRoomEmployeeRepository.countByChatRoom_IdAndActive(chatRoomId, true);
        if (memberCount == 0) {
            chatMessageRepository.deleteByChatRoom_Id(chatRoomId);
            chatRoomRepository.deleteById(chatRoomId);
        }
    }

    /**
     * 사용자가 참여 중인 채팅방 목록을 조회한다.
     * @param employeeId 사용자 ID
     * @return 채팅방 목록 DTO 리스트
     */
    @Override
    public List<ReadRooms> readRooms(Long employeeId) {
        // 사용자가 참여 중(active=true)인 채팅방 목록 조회
        List<ChatRoom> rooms = chatRoomEmployeeRepository.findChatRoomsByEmployeeIds(employeeId);

        // 각 방에 대해 최신 메시지, 방 정보, 활성화 여부 등 DTO로 변환
        return rooms.stream()
                .map(room -> {
                    // 최신 메시지 조회 (없을 수 있음)
                    ChatMessage lastMsg = room.getMessages().stream()
                            .max(Comparator.comparing(ChatMessage::getSentAt))
                            .orElse(null);

                    String lastMessage = lastMsg != null ? lastMsg.getContent() : "";
                    LocalDateTime updatedAt = lastMsg != null ? lastMsg.getSentAt() : room.getCreatedAt();

                    // 본인에 해당하는 참여자 엔티티에서 active 상태 확인
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
                            active
                    );
                })
                // 최신 메시지 기준 내림차순 정렬
                .sorted(Comparator.comparing(ReadRooms::getUpdatedAt).reversed())
                .collect(Collectors.toList());
    }

    /**
     * 특정 채팅방의 상세 정보(메시지 목록 포함)를 조회한다.
     * @param roomId 채팅방 ID
     * @param employeeId 사용자 ID
     * @return 채팅방 상세 DTO
     */
    @Override
    public ReadRoom readRoom(Long roomId, Long employeeId) {
        System.out.println("readRoom 호출됨 - roomId: " + roomId + ", employeeId: " + employeeId);
        ChatRoom room = chatRoomRepository.findChatRoomWithMessages(roomId)
                .orElseThrow(() -> new RuntimeException("채팅방을 찾을 수 없습니다."));

        ChatRoomEmployee cru = chatRoomEmployeeRepository
                .findByChatRoom_IdAndEmployee_EmployeeId(roomId, employeeId)
                .orElseThrow(() -> new RuntimeException("참여자 정보를 찾을 수 없습니다."));

        LocalDateTime lastExitAt = cru.getLastExitAt();
        System.out.println("lastExitAt: " + lastExitAt);

        // 마지막 퇴장 이후 메시지만 필터링하여 메시지 목록 생성
        List<ChatMessages> messages = room.getMessages().stream()
                .peek(msg -> System.out.println("msgId: " + msg.getId() + ", sentAt: " + msg.getSentAt()))
                .filter(msg -> lastExitAt == null || msg.getSentAt().isAfter(lastExitAt))
                .map(msg -> new ChatMessages(
                        msg.getId(),
                        msg.getChatRoom().getId(),
                        msg.getSender().getEmployeeId(),
                        msg.getContent(),
                        msg.getSentAt()
                )).collect(Collectors.toList());

        System.out.println("반환되는 메시지 개수: " + messages.size());

        ReadRoom dto = new ReadRoom();
        dto.setRoomId(room.getId());
        dto.setTitle(room.getTitle());
        dto.setCreatedAt(room.getCreatedAt());
        dto.setMessages(messages);
        return dto;
    }

    /**
     * 사용자가 해당 방의 마지막 읽은 메시지 ID와 시각을 기록한다.
     * @param roomId 채팅방 ID
     * @param employeeId 사용자 ID
     * @param lastReadMessageId 마지막 읽은 메시지 ID
     */
    @Override
    public void markAsRead(Long roomId, Long employeeId, Long lastReadMessageId) {
        ChatRoomEmployee cru = chatRoomEmployeeRepository
                .findByChatRoom_IdAndEmployee_EmployeeId(roomId, employeeId)
                .orElseThrow(() -> new RuntimeException("채팅방-참여자 정보가 없습니다."));

        System.out.println("[읽음처리] roomId: " + roomId + ", employeeId: " + employeeId + ", lastReadMessageId: " + lastReadMessageId);

        cru.setLastReadMessageId(lastReadMessageId);
        cru.setLastReadAt(LocalDateTime.now());
        chatRoomEmployeeRepository.save(cru);

        System.out.println("[읽음처리] 저장된 ChatRoomEmployee: " + cru);
    }

    /**
     * 사용자가 읽지 않은 메시지 개수를 반환한다.
     * @param roomId 채팅방 ID
     * @param employeeId 사용자 ID
     * @return 읽지 않은 메시지 수
     */
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
        try {
            System.out.println("[서버] getAllUnreadCountsAsStringKey 호출 employeeId=" + employeeId);
            List<ChatRoomEmployee> participants = chatRoomEmployeeRepository.findByEmployee_EmployeeId(employeeId);
            if (participants == null || participants.isEmpty()) {
                System.out.println("[서버] 참여중인 채팅방 없음");
                return result;
            }
            for (ChatRoomEmployee p : participants) {
                if (p.getChatRoom() == null) {
                    System.out.println("[서버] chatRoom이 null인 참여자 발견");
                    continue;
                }
                Long roomId = p.getChatRoom().getId();
                if (roomId == null) {
                    System.out.println("[서버] roomId가 null인 참여자 발견");
                    continue;
                }
                Long lastReadId = p.getLastReadMessageId() == null ? 0L : p.getLastReadMessageId();
                int count = 0;
                try {
                    count = chatMessageRepository.countUnreadMessages(roomId, lastReadId);
                    System.out.println("[서버] 방 " + roomId + "의 unreadCount=" + count + " (lastReadId=" + lastReadId + ")");
                } catch (Exception e) {
                    System.err.println("[서버] countUnreadMessages 에러: " + e.getMessage());
                }
                result.put(String.valueOf(roomId), count);
            }
            System.out.println("[서버] 최종 unreadCount Map: " + result);
        } catch (Exception e) {
            System.err.println("[서버] getAllUnreadCountsAsStringKey 전체 에러: " + e.getMessage());
            e.printStackTrace();
        }
        return result;
    }


    /**
     * 메시지 전송 처리 (필요시 채팅방 및 참여자 추가)
     * @param sendChat 메시지 전송 요청 DTO
     */
    @Override
    public void send(SendChat sendChat) {
        ChatRoom room;

        // roomId가 없으면 새 채팅방 생성, 있으면 기존 방 사용
        if (sendChat.getRoomId() == null) {
            room = new ChatRoom();
            room.setTitle(sendChat.getTitle() != null ? sendChat.getTitle() : "새 채팅방");
            room = chatRoomRepository.save(room);
        } else {
            room = chatRoomRepository.findById(sendChat.getRoomId())
                    .orElseThrow(() -> new RuntimeException("채팅방이 없습니다."));
        }

        // 참여자 목록이 있으면 모두 추가, 없으면 sender만 추가
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

        // 메시지 엔티티 생성 및 저장
        Employee sender = employeeRepository.findById(sendChat.getSenderId())
                .orElseThrow(() -> new RuntimeException("사용자가 없습니다."));
        ChatMessage message = new ChatMessage();
        message.setChatRoom(room);
        message.setSender(sender);
        message.setContent(sendChat.getMessage());
        message.setSentAt(LocalDateTime.now());
        chatMessageRepository.save(message);
    }
}
