package kh.gangnam.b2b.service.ServiceImpl;

import kh.gangnam.b2b.dto.approval.request.LeaveApprovalRequest;
import kh.gangnam.b2b.dto.approval.response.LeaveApprovalResponse;
import kh.gangnam.b2b.dto.work.response.leave.LeaveRequestResponse;
import kh.gangnam.b2b.entity.work.ApprovalStatus;
import kh.gangnam.b2b.entity.work.LeaveRequest;
import kh.gangnam.b2b.exception.InvalidRequestException;
import kh.gangnam.b2b.exception.NotFoundException;
import kh.gangnam.b2b.repository.work.LeaveRequestRepository;
import kh.gangnam.b2b.service.ApprovalService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ApprovalServiceImpl implements ApprovalService {

    private final LeaveRequestRepository leaveRequestRepository;

    private LeaveRequest getLeaveRequest(Long id) {
        return leaveRequestRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("연차 요청 정보를 찾을 수 없습니다."));
    }

    @Override
    public LeaveApprovalResponse processApproval(Long requestId, Long employeeId, LeaveApprovalRequest req) {
        LeaveRequest leaveRequest = getLeaveRequest(requestId);
        ApprovalStatus status = req.getApprovalStatus();

        if (status == ApprovalStatus.APPROVED) {
            leaveRequest.setStatus(ApprovalStatus.APPROVED);
            leaveRequest.setRejectReason(null);
        } else if (status == ApprovalStatus.REJECTED) {
            leaveRequest.setStatus(ApprovalStatus.REJECTED);
            leaveRequest.setRejectReason(req.getRejectReason());
        } else {
            throw new InvalidRequestException("잘못된 승인 상태입니다.");
        }

        leaveRequestRepository.save(leaveRequest);

        LeaveApprovalResponse response = LeaveApprovalResponse.fromEntity(leaveRequest);
        if (status == ApprovalStatus.REJECTED) response.setRejectReason(req.getRejectReason());
        return response;
    }

    @Override
    public List<LeaveRequestResponse> getPendingRequests(Long employeeId) {
        return leaveRequestRepository.findByStatus(ApprovalStatus.PENDING)
                .stream().map(LeaveRequestResponse::fromEntity).toList();
    }

    @Override
    public List<LeaveRequestResponse> getCompletedRequests(Long employeeId) {
        return leaveRequestRepository.findByStatusIn(Arrays.asList(ApprovalStatus.APPROVED, ApprovalStatus.REJECTED))
                .stream().map(LeaveRequestResponse::fromEntity).toList();
    }
}
