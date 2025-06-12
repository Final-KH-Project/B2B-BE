package kh.gangnam.b2b.repository.work;

import kh.gangnam.b2b.entity.auth.Employee;
import kh.gangnam.b2b.entity.work.ApprovalStatus;
import kh.gangnam.b2b.entity.work.LeaveRequestEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface LeaveRequestRepository extends JpaRepository<LeaveRequestEntity, Long> {

    List<LeaveRequestEntity> findByEmployeeAndStatus(Employee employee, ApprovalStatus status);

    List<LeaveRequestEntity> findByEmployee(Employee employee);

    // ✅ 반차 신청 시 단일 날짜 기준 겹침 조회
    @Query("SELECT l FROM leave_request l " +
            "WHERE l.employee = :employee " +
            "AND l.status = 'APPROVED' " +
            "AND :date BETWEEN l.startDate AND l.endDate")
    List<LeaveRequestEntity> findApprovedLeaveForDate(@Param("employee") Employee employee,
                                                @Param("date") LocalDate date);

    // ✅ 연속 날짜 신청 시 범위 겹침 조회
    @Query("SELECT lr FROM leave_request lr " +
            "WHERE lr.employee = :employee " +
            "AND lr.status = 'APPROVED' " +
            "AND lr.startDate <= :endDate " +
            "AND lr.endDate >= :startDate")
    List<LeaveRequestEntity> findApprovedInRange(@Param("employee") Employee employee,
                                           @Param("startDate") LocalDate start,
                                           @Param("endDate") LocalDate end);

    List<LeaveRequestEntity> findByStatusIn(List<ApprovalStatus> list);

    List<LeaveRequestEntity> findByStatus(ApprovalStatus approvalStatus);

    }
