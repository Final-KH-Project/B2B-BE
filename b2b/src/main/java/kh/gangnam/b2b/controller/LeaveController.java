package kh.gangnam.b2b.controller;

import kh.gangnam.b2b.config.security.CustomEmployeeDetails;
import kh.gangnam.b2b.dto.work.request.leave.LeaveRequest;
import kh.gangnam.b2b.dto.work.response.ApiResponse;
import kh.gangnam.b2b.dto.work.response.leave.LeaveRequestResponse;
import kh.gangnam.b2b.dto.work.response.leave.LeaveStatusResponse;
import kh.gangnam.b2b.entity.work.LeaveRequestEntity;
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
            @RequestBody LeaveRequest request
    ) {
        System.out.println("현재 로그인 사용자 ID:" + user.getEmployeeId());

        leaveRequestService.applyLeave(user.getEmployeeId(), request);
        return ResponseEntity.ok(ApiResponse.success("연차 신청 완료"));
    }

    @GetMapping("/status")
    public  ResponseEntity<LeaveStatusResponse> getLeaveStatus(
            @AuthenticationPrincipal CustomEmployeeDetails user){
        // 현재 로그인한 사원의 ID 추출
        LeaveStatusResponse status = leaveRequestService.getLeaveStatus(user.getEmployeeId());
        // 연차 현황 반환 (총연차, 사용연차, 남은연차, 퍼센트)
        return ResponseEntity.ok(status);
    }

    //연차 신정내역 조회 API
    //사용자가 지금까지 신청한 연차/반타/출장 내역을 확인할 때 호출

    @GetMapping("/my")
    public ResponseEntity<List<LeaveRequestResponse>> getMyLeaveRequests(
            @AuthenticationPrincipal CustomEmployeeDetails user){
        List<LeaveRequestEntity> list = leaveRequestService.getMyRequests(user.getEmployeeId());
        List<LeaveRequestResponse> result = list.stream()
                .map(LeaveRequestResponse::fromEntity)
                .toList();

        return ResponseEntity.ok(result);
    }


}
