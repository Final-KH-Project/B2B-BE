package kh.gangnam.b2b.controller;

import kh.gangnam.b2b.config.security.CustomEmployeeDetails;
import kh.gangnam.b2b.dto.approval.request.LeaveApprovalRequest;
import kh.gangnam.b2b.dto.approval.response.LeaveApprovalResponse;
import kh.gangnam.b2b.dto.work.response.leave.LeaveRequestResponse;
import kh.gangnam.b2b.service.ApprovalService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/approval")
public class ApprovalController {

    private final ApprovalService approvalService;

    /** 승인 대기 목록 조회 (부서장만 가능) */
    @GetMapping("/pending")
    public ResponseEntity<List<LeaveRequestResponse>> getPendingRequests(
            @AuthenticationPrincipal CustomEmployeeDetails user) {
        return ResponseEntity.ok(approvalService.getPendingRequests(user.getEmployeeId()));
    }

    /** 완료(승인/반려) 목록 조회 */
    @GetMapping("/completed")
    public ResponseEntity<List<LeaveRequestResponse>> getCompletedRequests(
            @AuthenticationPrincipal CustomEmployeeDetails user) {
        return ResponseEntity.ok(approvalService.getCompletedRequests(user.getEmployeeId()));
    }

    /** 결재자 승인/반려 처리 */
    @PostMapping("/{requestId}/decision")
    public ResponseEntity<LeaveApprovalResponse> processApproval(
            @PathVariable("requestId") Long requestId,
            @AuthenticationPrincipal CustomEmployeeDetails user,
            @RequestBody LeaveApprovalRequest approvalRequest) {
        return ResponseEntity.ok(
                approvalService.processApproval(requestId, user.getEmployeeId(), approvalRequest)
        );
    }
}
