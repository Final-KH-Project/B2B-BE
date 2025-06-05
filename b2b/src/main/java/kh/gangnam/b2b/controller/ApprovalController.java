package kh.gangnam.b2b.controller;

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

    // 승인 대기 목록 조회 (부서장만 가능)
    @GetMapping("/pending")
    public ResponseEntity<List<LeaveRequestResponse>> getPendingRequests(
            @AuthenticationPrincipal(expression = "employeeId") Long employeeId,
            @AuthenticationPrincipal(expression = "role") String role) {
        // 여기서 실제 값이 잘 들어오는지 확인
        System.out.println("employeeId: " + employeeId + ", role: " + role);

        if (!"ROLE_HEAD".equals(role)) {
            return ResponseEntity.status(403).build();
        }
        List<LeaveRequestResponse> result = approvalService.getPendingRequests(employeeId);
        return ResponseEntity.ok(result);
    }


    // 결재자 승인/반려 처리
    @PostMapping("/{requestId}/decision")
    public ResponseEntity<LeaveApprovalResponse> processApproval(
            @PathVariable("requestId") Long requestId,
            @AuthenticationPrincipal(expression = "employeeId") Long employeeId,
            @AuthenticationPrincipal(expression = "role") String role,
            @RequestBody LeaveApprovalRequest approvalRequest) {
        // 권한 체크: ROLE_HEAD만 접근 가능
        if (!"ROLE_HEAD".equals(role)) {
            return ResponseEntity.status(403).build();
        }
        LeaveApprovalResponse response = approvalService.processApproval(requestId, employeeId, approvalRequest);
        return ResponseEntity.ok(response);
    }
}
