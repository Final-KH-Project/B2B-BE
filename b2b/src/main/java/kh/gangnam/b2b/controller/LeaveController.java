package kh.gangnam.b2b.controller;

import kh.gangnam.b2b.config.security.CustomEmployeeDetails;
import kh.gangnam.b2b.dto.work.request.leave.LeaveRequestRequest;

import kh.gangnam.b2b.dto.work.response.ApiResponse;
import kh.gangnam.b2b.dto.work.response.leave.LeaveRequestResponse;
import kh.gangnam.b2b.dto.work.response.leave.LeaveStatusResponse;
import kh.gangnam.b2b.service.LeaveRequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/leave")
public class LeaveController {

    private final LeaveRequestService leaveRequestService;

    @PostMapping("/apply")
    public ResponseEntity<?> applyLeave(
            @AuthenticationPrincipal CustomEmployeeDetails user,
            @RequestBody LeaveRequestRequest request
    ) {
        leaveRequestService.applyLeave(user.getEmployeeId(), request);
        return ResponseEntity.ok(ApiResponse.success("연차 신청 완료"));
    }

    @GetMapping("/status")
    public ResponseEntity<ApiResponse<LeaveStatusResponse>> getLeaveStatus(
            @AuthenticationPrincipal CustomEmployeeDetails user) {

        LeaveStatusResponse status = leaveRequestService.getLeaveStatus(user.getEmployeeId());

        // ✅ 제네릭 명시로 data 응답 보장
        return ResponseEntity.ok(ApiResponse.<LeaveStatusResponse>success(status));
    }

    //연차 신정내역 조회 API
    //사용자가 지금까지 신청한 연차/반타/출장 내역을 확인할 때 호출
    @GetMapping("/my")
    public ResponseEntity<ApiResponse<List<LeaveRequestResponse>>> getMyLeaveRequests(
            @AuthenticationPrincipal CustomEmployeeDetails user){

        List<LeaveRequestResponse> result = leaveRequestService.getMyRequests(user.getEmployeeId());
        return ResponseEntity.ok(ApiResponse.success(result));
    }


}
