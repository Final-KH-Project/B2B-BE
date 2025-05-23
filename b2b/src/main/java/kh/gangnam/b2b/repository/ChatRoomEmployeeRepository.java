package kh.gangnam.b2b.repository;

import kh.gangnam.b2b.entity.chat.ChatRoom;
import kh.gangnam.b2b.entity.chat.ChatRoomEmployee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

/**
 * 채팅방-유저 중간 테이블 Repository
 * - userId로 내가 속한 채팅방 리스트 조회
 * - 유저가 채팅방에 속해있는지 여부 확인
 */

public interface ChatRoomEmployeeRepository extends JpaRepository<ChatRoomEmployee, Long> {
    @Query("SELECT cru.chatRoom FROM ChatRoomEmployee cru WHERE cru.employee.employeeId = :employeeId")
    List<ChatRoom> findChatRoomsByEmployeeId(@Param("employeeId") Long employeeId);
    @Query("SELECT COUNT(cru) > 0 FROM ChatRoomEmployee cru WHERE cru.employee.employeeId = :employeeId AND cru.chatRoom.id = :chatRoomId")
    boolean existsEmployeeInChatRoom(@Param("employeeId") Long employeeId, @Param("chatRoomId") Long chatRoomId);
}

