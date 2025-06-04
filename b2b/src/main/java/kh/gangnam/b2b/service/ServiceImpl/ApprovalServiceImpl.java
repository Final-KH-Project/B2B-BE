package kh.gangnam.b2b.service.impl;

import kh.gangnam.b2b.dto.approval.request.LeaveApprovalRequest;
import kh.gangnam.b2b.dto.approval.response.LeaveApprovalResponse;
import kh.gangnam.b2b.entity.work.ApprovalStatus;
import kh.gangnam.b2b.entity.work.LeaveRequest;
import kh.gangnam.b2b.repository.work.LeaveRequestRepository;
import kh.gangnam.b2b.service.ApprovalService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ApprovalServiceImpl implements ApprovalService {

    private final LeaveRequestRepository leaveRequestRepository;

    @Override
    public LeaveApprovalResponse processApproval(Long requestId, Long employeeId, LeaveApprovalRequest approvalRequest) {
        LeaveRequest leaveRequest = leaveRequestRepository.findById(requestId)
                .orElseThrow(() -> new IllegalArgumentException("연차 요청 정보를 찾을 수 없습니다."));

        // 권한 체크
        if (!leaveRequest.getApprover().getEmployeeId().equals(employeeId)) {
            throw new AccessDeniedException("해당 부서장만 승인/반려할 수 있습니다.");
        }

        // 승인/반려 상태만 엔티티에 반영 (반려사유는 엔티티에 저장하지 않음)
        if (approvalRequest.getApprovalStatus() == ApprovalStatus.APPROVED) {
            leaveRequest.setStatus(ApprovalStatus.APPROVED);
        } else if (approvalRequest.getApprovalStatus() == ApprovalStatus.REJECTED) {
            leaveRequest.setStatus(ApprovalStatus.REJECTED);
            // 반려사유는 엔티티에 저장하지 않고, 아래에서 응답 DTO에만 세팅
        } else {
            throw new IllegalArgumentException("잘못된 승인 상태입니다.");
        }

        leaveRequestRepository.save(leaveRequest);

        // 응답 DTO 생성 (반려사유는 DTO에만 세팅)
        LeaveApprovalResponse response = LeaveApprovalResponse.fromEntity(leaveRequest);
        if (approvalRequest.getApprovalStatus() == ApprovalStatus.REJECTED) {
            response.setRejectReason(approvalRequest.getRejectReason());
        }
        return response;
    }
}
