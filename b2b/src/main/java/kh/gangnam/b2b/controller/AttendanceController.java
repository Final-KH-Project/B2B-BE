package kh.gangnam.b2b.controller;

import kh.gangnam.b2b.config.security.CustomEmployeeDetails;
import kh.gangnam.b2b.dto.work.request.attendance.ClockInRequest;
import kh.gangnam.b2b.dto.work.request.attendance.ClockOutRequest;
import kh.gangnam.b2b.dto.work.response.attendance.WeeklyAttendanceResponse;
import kh.gangnam.b2b.service.AttendanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/attendance")
public class AttendanceController {

    private final AttendanceService attendanceService;

    /*
    * 출근 처리 API
    * 사용자가 출근 버튼 눌렀을때 호출
    * ClockInRequest에서 출근시간을 전달 받을 수 있으며, 없으면 서버시간 사용
    * 서버시간 = (LocalDateTime.now())
    */
    @PostMapping("/check-in")
    public ResponseEntity<Void> clockIn(
            @AuthenticationPrincipal CustomEmployeeDetails user,
            @RequestBody(required = false)ClockInRequest request //요청 바디없을 수 있음
            ){
        attendanceService.clockIn(user.getEmployeeId(), request);
        return ResponseEntity.ok().build();
    }
    @PostMapping("/check-out")
    public ResponseEntity<ClockOutRequest>clockOut(
            @AuthenticationPrincipal CustomEmployeeDetails user,
            @RequestBody(required = false) ClockOutRequest request
    ){
        attendanceService.clockOut(user.getEmployeeId(), request);
        return ResponseEntity.ok().build();
    }
//    @PostMapping("/apply")
//    public ResponseEntity<ApiResponse> applyWork(
//            @AuthenticationPrincipal CustomEmployeeDetails user,
//            @RequestBody WorkApplyRequest request
//    ){
//        attendanceService.applyWork(user.getEmployeeId(), request);
//        return ResponseEntity.ok(ApiResponse.success("신청완료"));
//    }

    @GetMapping("/week")
    public ResponseEntity<WeeklyAttendanceResponse> getWeeklyAttendance(
            @AuthenticationPrincipal CustomEmployeeDetails user,
            @RequestParam("referenceDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate referenceDate
    ){
        WeeklyAttendanceResponse response = attendanceService.getWeeklyAttendance(user.getEmployeeId(), referenceDate);
        return ResponseEntity.ok(response);
    }

}
