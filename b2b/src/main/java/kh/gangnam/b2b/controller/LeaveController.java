package kh.gangnam.b2b.controller;

import kh.gangnam.b2b.dto.auth.CustomEmployeeDetails;
import kh.gangnam.b2b.dto.work.LeaveRequestDTO;
import kh.gangnam.b2b.dto.work.response.leave.LeaveRequestResponse;
import kh.gangnam.b2b.dto.work.response.leave.LeaveStatusResponse;
import kh.gangnam.b2b.entity.work.LeaveRequest;
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
    public ResponseEntity<?> applyLeave(@AuthenticationPrincipal CustomEmployeeDetails user,
                                        @RequestBody LeaveRequestDTO request) {
        leaveRequestService.applyLeave(user.getEmployeeId(), request);
        return ResponseEntity.ok("연차 신청 완료");
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
        List<LeaveRequest> list = leaveRequestService.getMyRequests(user.getEmployeeId());
        List<LeaveRequestResponse> result = list.stream()
                .map(LeaveRequestResponse::from)
                .toList();

        return ResponseEntity.ok(result);
    }


}
