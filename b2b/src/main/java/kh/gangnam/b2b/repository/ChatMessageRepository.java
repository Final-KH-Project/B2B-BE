package kh.gangnam.b2b.repository;

import kh.gangnam.b2b.entity.chat.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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

    // 해당 방에서 lastReadMessageId보다 큰 메시지 개수
    @Query("SELECT COUNT(m) FROM ChatMessage m WHERE m.chatRoom.id = :roomId AND m.id > :lastReadMessageId")
    int countUnreadMessages(@Param("roomId") Long roomId, @Param("lastReadMessageId") Long lastReadMessageId);
}
