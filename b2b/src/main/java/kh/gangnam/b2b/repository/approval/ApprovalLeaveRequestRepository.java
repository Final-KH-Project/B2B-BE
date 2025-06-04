package kh.gangnam.b2b.repository.approval;

import kh.gangnam.b2b.entity.work.ApprovalStatus;
import kh.gangnam.b2b.entity.work.LeaveRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ApprovalLeaveRequestRepository {
    public interface LeaveRequestRepository extends JpaRepository<LeaveRequest, Long> {

        // 결재자 기준 전체 연차 요청 조회
        List<LeaveRequest> findByApprover_EmployeeId(Long approverId);

        // 결재자 기준 승인 대기(PENDING) 연차 요청 조회
        List<LeaveRequest> findByApprover_EmployeeIdAndStatus(Long approverId, ApprovalStatus status);
    }


    List<LeaveRequest> findByApproverIdAndStatus(Long approverId, ApprovalStatus status);
}
