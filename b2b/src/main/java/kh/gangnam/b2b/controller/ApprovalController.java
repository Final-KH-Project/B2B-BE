package kh.gangnam.b2b.controller;

import kh.gangnam.b2b.dto.approval.request.LeaveApprovalRequest;
import kh.gangnam.b2b.dto.approval.response.LeaveApprovalResponse;
import kh.gangnam.b2b.service.ApprovalService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/approval")
public class ApprovalController {

    private final ApprovalService approvalService;

    // 결재자 승인/반려 처리 (반려사유는 DTO에서만 관리)
    @PostMapping("/{requestId}/decision")
    public ResponseEntity<LeaveApprovalResponse> processApproval(
            @PathVariable("requestId") Long requestId,
            @AuthenticationPrincipal(expression = "employeeId") Long employeeId,
            @RequestBody LeaveApprovalRequest approvalRequest) {
        LeaveApprovalResponse response = approvalService.processApproval(requestId, employeeId, approvalRequest);
        return ResponseEntity.ok(response);
    }
}
