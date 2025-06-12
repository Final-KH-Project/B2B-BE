package kh.gangnam.b2b.dto.approval.request;


import kh.gangnam.b2b.entity.work.ApprovalStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LeaveApprovalRequest {
    private Long leaveRequestId;           // 결재 대상 연차 요청 ID
    private Long approverId;               // 결재자(부서장) ID
    private ApprovalStatus approvalStatus; // APPROVED, REJECTED 중 하나
    private String rejectReason;           // 반려 사유 (REJECTED일 때만 사용)
}
