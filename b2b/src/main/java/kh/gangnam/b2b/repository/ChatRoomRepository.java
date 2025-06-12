package kh.gangnam.b2b.repository;

import kh.gangnam.b2b.entity.chat.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

/**
 * 채팅방 Repository
 * - 채팅방 ID로 채팅방 + 메시지 리스트를 한 번에 조회할 수 있는 쿼리 포함
 * - 채팅방 선택 시, 해당 채팅방과 메시지 내역을 한 번에 가져오는 데 사용.
 */
public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {
    @Query("SELECT DISTINCT c FROM ChatRoom c LEFT JOIN FETCH c.messages WHERE c.id = :roomId")
    Optional<ChatRoom> findChatRoomWithMessages(@Param("roomId") Long roomId);
}
