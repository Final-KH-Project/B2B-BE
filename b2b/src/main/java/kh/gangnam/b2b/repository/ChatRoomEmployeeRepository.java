package kh.gangnam.b2b.repository;

import kh.gangnam.b2b.entity.chat.ChatRoom;
import kh.gangnam.b2b.entity.chat.ChatRoomEmployee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

/**
 * 채팅방-유저 중간 테이블 Repository
 * - userId로 내가 속한 채팅방 리스트 조회
 * - 유저가 채팅방에 속해있는지 여부 확인
 */

public interface ChatRoomEmployeeRepository extends JpaRepository<ChatRoomEmployee, Long> {
    @Query("SELECT cru.chatRoom FROM ChatRoomEmployee cru WHERE cru.employee.employeeId = :employeeId")
    List<ChatRoom> findChatRoomsByEmployeeIds(@Param("employeeId") Long employeeId);
    @Query("SELECT cru.chatRoom FROM ChatRoomEmployee cru WHERE cru.employee.employeeId = :employeeId AND cru.active = true")
    List<ChatRoom> findChatRoomsByEmployeeId(@Param("employeeId") Long employeeId);
    @Query("SELECT cru.chatRoom FROM ChatRoomEmployee cru " +
            "WHERE cru.employee.employeeId IN :employeeIds " +
            "GROUP BY cru.chatRoom " +
            "HAVING COUNT(DISTINCT cru.employee.employeeId) = :size")
    List<ChatRoom> findAllRoomsByEmployeeIds(@Param("employeeIds") List<Long> employeeIds, @Param("size") long size);
    List<ChatRoomEmployee> findByEmployee_EmployeeId(Long employeeId);

    //방에서 남은 참여 인원 수
    int countByChatRoom_IdAndActive(Long chatRoomId, Boolean active);

    @Query("SELECT COUNT(cru) > 0 FROM ChatRoomEmployee cru WHERE cru.employee.employeeId = :employeeId AND cru.chatRoom.id = :chatRoomId")
    boolean existsEmployeeInChatRoom(@Param("employeeId") Long employeeId, @Param("chatRoomId") Long chatRoomId);

    // 특정 방에서 특정 사원(employeeId)만 삭제
    void deleteByChatRoom_IdAndEmployee_EmployeeId(Long chatRoomId, Long employeeId);

    // 채팅방 ID와 직원 ID로 중간 엔티티 조회
    Optional<ChatRoomEmployee> findByChatRoom_IdAndEmployee_EmployeeId(Long roomId, Long employeeId);

}