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

        @Query("SELECT l FROM LeaveRequest l " +
                "WHERE l.employee = :employee " +
                "AND l.status = kh.gangnam.b2b.entity.work.ApprovalStatus.APPROVED " +
                "AND :date BETWEEN l.startDate AND l.endDate")
        List<LeaveRequestEntity> findApprovedLeaveForDate(@Param("employee") Employee employee,
                                                          @Param("date") LocalDate date);

        List<LeaveRequestEntity> findByStatusIn(List<ApprovalStatus> list);

        List<LeaveRequestEntity> findByStatus(ApprovalStatus approvalStatus);

    }
