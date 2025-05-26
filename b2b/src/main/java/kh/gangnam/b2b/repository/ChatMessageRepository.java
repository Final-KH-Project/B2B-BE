package kh.gangnam.b2b.repository;

import kh.gangnam.b2b.entity.chat.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

/**
 * 채팅 메시지 Repository
 * - 채팅방 ID로 해당 방의 메시지를 시간순으로 조회
 * - 단독으로는 메시지 조회 불가, 반드시 채팅방 ID 필요
 */
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    List<ChatMessage> findByChatRoomIdOrderBySentAtAsc(Long chatRoomId);

    // 특정 방의 모든 메시지 삭제
    void deleteByChatRoom_Id(Long chatRoomId);
}
