package kh.gangnam.b2b.service.ServiceImpl;

import kh.gangnam.b2b.dto.approval.request.LeaveApprovalRequest;
import kh.gangnam.b2b.dto.approval.response.LeaveApprovalResponse;
import kh.gangnam.b2b.dto.work.response.leave.LeaveRequestResponse;
import kh.gangnam.b2b.entity.work.ApprovalStatus;
import kh.gangnam.b2b.entity.work.LeaveRequest;
import kh.gangnam.b2b.repository.approval.ApprovalLeaveRequestRepository;
import kh.gangnam.b2b.repository.work.LeaveRequestRepository;
import kh.gangnam.b2b.service.ApprovalService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ApprovalServiceImpl implements ApprovalService {

    private final LeaveRequestRepository leaveRequestRepository;

    @Override
    public LeaveApprovalResponse processApproval(Long requestId, Long employeeId, LeaveApprovalRequest approvalRequest) {
        LeaveRequest leaveRequest = leaveRequestRepository.findById(requestId)
                .orElseThrow(() -> new IllegalArgumentException("연차 요청 정보를 찾을 수 없습니다."));


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

    @Override
    public List<LeaveRequestResponse> getPendingRequests(Long employeeId) {
        // 결재자 조건 없이 상태가 PENDING인 모든 연차 요청 조회
        List<LeaveRequest> pendingRequests = leaveRequestRepository
                .findByStatus(ApprovalStatus.PENDING);

        return pendingRequests.stream()
                .map(LeaveRequestResponse::fromEntity)
                .collect(Collectors.toList());
    }

    // [추가] 완료(승인/반려) 목록 조회
    @Override
    public List<LeaveRequestResponse> getCompletedRequests(Long employeeId) {
        // 상태가 APPROVED 또는 REJECTED인 연차 요청만 조회
        List<LeaveRequest> completedRequests = leaveRequestRepository
                .findByStatusIn(Arrays.asList(ApprovalStatus.APPROVED, ApprovalStatus.REJECTED));

        return completedRequests.stream()
                .map(LeaveRequestResponse::fromEntity)
                .collect(Collectors.toList());
    }

}
