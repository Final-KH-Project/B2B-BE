package kh.gangnam.b2b.service;

import kh.gangnam.b2b.dto.approval.request.LeaveApprovalRequest;
import kh.gangnam.b2b.dto.approval.response.LeaveApprovalResponse;
import kh.gangnam.b2b.dto.work.response.leave.LeaveRequestResponse;

import java.util.List;

public interface ApprovalService {
    // 결재자(부서장)가 연차 요청을 승인/반려 처리 (반려사유는 DTO에서만 관리)
    LeaveApprovalResponse processApproval(Long requestId, Long employeeId, LeaveApprovalRequest approvalRequest);

    List<LeaveRequestResponse> getPendingRequests(Long employeeId);
}
