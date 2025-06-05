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

    // 승인 대기 목록 조회 (부서장만 가능)
    @GetMapping("/pending")
    public ResponseEntity<List<LeaveRequestResponse>> getPendingRequests(
            @AuthenticationPrincipal CustomEmployeeDetails details) {

        Long employeeId = details.getEmployeeId();
        String role = details.getRole();

        List<LeaveRequestResponse> result = approvalService.getPendingRequests(employeeId);
        return ResponseEntity.ok(result);
    }

    // 완료(승인/반려) 목록 조회 (예시: 모든 승인/반려된 휴가 요청)
    @GetMapping("/completed")
    public ResponseEntity<List<LeaveRequestResponse>> getCompletedRequests(
            @AuthenticationPrincipal CustomEmployeeDetails details) {

        Long employeeId = details.getEmployeeId();
        String role = details.getRole();

        // status가 APPROVED 또는 REJECTED인 요청만 반환하도록 서비스에서 처리
        List<LeaveRequestResponse> result = approvalService.getCompletedRequests(employeeId);
        return ResponseEntity.ok(result);
    }


    // 결재자 승인/반려 처리
    @PostMapping("/{requestId}/decision")
    public ResponseEntity<LeaveApprovalResponse> processApproval(
            @PathVariable("requestId") Long requestId,
            @AuthenticationPrincipal(expression = "employeeId") Long employeeId,
            @RequestBody LeaveApprovalRequest approvalRequest) {
        LeaveApprovalResponse response = approvalService.processApproval(requestId, employeeId, approvalRequest);
        return ResponseEntity.ok(response);
    }
}
