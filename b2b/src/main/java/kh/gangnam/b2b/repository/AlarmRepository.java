package kh.gangnam.b2b.repository;

import kh.gangnam.b2b.entity.alarm.Alarm;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


public interface AlarmRepository extends JpaRepository<Alarm, Long> {
    //읽지 않은 알림 개수
    Integer countByEmployee_employeeIdAndIsReadFalse(Long employeeId);

    Integer countByEmployee_loginIdAndIsReadFalse(String loginId);


    // 안 읽은 알림 조회용
    List<Alarm> findByEmployee_employeeIdAndIsReadFalse(Long employeeId);

    // 읽음 처리
    @Transactional
    @Modifying
    @Query("UPDATE Alarm a SET a.isRead = true WHERE a.employee.employeeId = :employeeId AND a.board.boardId = :boardId")
    void markAsRead(@Param("employeeId") Long employeeId, @Param("boardId") Long boardId);

    //전체 읽음 처리
    @Transactional
    @Modifying
    @Query("UPDATE Alarm a SET a.isRead = true WHERE a.employee.employeeId = :employeeId AND a.isRead = false")
    int markAllAsReadByEmployeeId(@Param("employeeId") Long employeeId);
}
