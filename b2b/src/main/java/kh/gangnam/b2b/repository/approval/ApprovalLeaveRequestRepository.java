package kh.gangnam.b2b.repository.approval;

import kh.gangnam.b2b.entity.work.ApprovalStatus;
import kh.gangnam.b2b.entity.work.LeaveRequestEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ApprovalLeaveRequestRepository {
    public interface LeaveRequestRepository extends JpaRepository<LeaveRequestEntity, Long> {

        // 결재자 기준 전체 연차 요청 조회
        List<LeaveRequestEntity> findByApprover_EmployeeId(Long approverId);

        // 결재자 기준 승인 대기(PENDING) 연차 요청 조회
        List<LeaveRequestEntity> findByApprover_EmployeeIdAndStatus(Long approverId, ApprovalStatus status);
    }


    List<LeaveRequestEntity> findByApproverIdAndStatus(Long approverId, ApprovalStatus status);

}
