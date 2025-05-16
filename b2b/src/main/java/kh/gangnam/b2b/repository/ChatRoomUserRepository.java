package kh.gangnam.b2b.repository;

import kh.gangnam.b2b.entity.chat.ChatRoom;
import kh.gangnam.b2b.entity.chat.ChatRoomUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

/**
 * 채팅방-유저 중간 테이블 Repository
 * - userId로 내가 속한 채팅방 리스트 조회
 * - 유저가 채팅방에 속해있는지 여부 확인
 */

public interface ChatRoomUserRepository extends JpaRepository<ChatRoomUser, Long> {
    @Query("SELECT cru.chatRoom FROM ChatRoomUser cru WHERE cru.user.userId = :userId")
    List<kh.gangnam.b2b.entity.chat.ChatRoom> findChatRoomsByUserId(@Param("userId") Long userId);

    @Query("SELECT COUNT(cru) > 0 FROM ChatRoomUser cru WHERE cru.user.userId = :userId AND cru.chatRoom.id = :chatRoomId")
    boolean existsUserInChatRoom(@Param("userId") Long userId, @Param("chatRoomId") Long chatRoomId);
}

