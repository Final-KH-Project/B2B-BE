package kh.gangnam.b2b.controller;

import kh.gangnam.b2b.config.security.CustomEmployeeDetails;
import kh.gangnam.b2b.dto.work.request.attendance.CheckInRequest;
import kh.gangnam.b2b.dto.work.request.attendance.CheckoutRequest;
import kh.gangnam.b2b.dto.work.response.ApiResponse;
import kh.gangnam.b2b.dto.work.response.attendance.DailyAttendanceResponse;
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
    public ResponseEntity<ApiResponse<String>> checkIn(
            @AuthenticationPrincipal CustomEmployeeDetails user,
            @RequestBody(required = false) CheckInRequest request
            ){
        attendanceService.checkIn(user.getEmployeeId(), request);
        return ResponseEntity.ok(ApiResponse.success("출근이 정상적으로 처리되었습니다."));
    }
    @PostMapping("/check-out")
    public ResponseEntity<ApiResponse<DailyAttendanceResponse>> clockOut(
            @AuthenticationPrincipal CustomEmployeeDetails details,
            @RequestBody(required = false) CheckoutRequest request
    ){
        DailyAttendanceResponse result = attendanceService.checkOut(details.getEmployeeId(), request);
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    @GetMapping("/week")
    public ResponseEntity<ApiResponse<WeeklyAttendanceResponse>> getWeeklyAttendance(
            @AuthenticationPrincipal CustomEmployeeDetails user,
            @RequestParam("referenceDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate referenceDate
    ){
        WeeklyAttendanceResponse response = attendanceService.getWeeklyAttendance(user.getEmployeeId(), referenceDate);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

}
