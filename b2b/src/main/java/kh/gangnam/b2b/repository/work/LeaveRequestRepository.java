package kh.gangnam.b2b.repository.work;

import kh.gangnam.b2b.entity.auth.Employee;
import kh.gangnam.b2b.entity.work.ApprovalStatus;
import kh.gangnam.b2b.entity.work.LeaveRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LeaveRequestRepository extends JpaRepository<LeaveRequest, Long> {

    List<LeaveRequest> findByEmployeeAndStatus(Employee employee, ApprovalStatus status);

    List<LeaveRequest> findByEmployee(Employee employee);

    List<LeaveRequest> findByApprover_EmployeeIdAndStatus(Long approverId, ApprovalStatus status);

    List<LeaveRequest> findByStatus(ApprovalStatus approvalStatus);
}
