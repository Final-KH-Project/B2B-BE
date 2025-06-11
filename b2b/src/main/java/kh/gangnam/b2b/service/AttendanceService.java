package kh.gangnam.b2b.service;

import kh.gangnam.b2b.dto.work.request.attendance.CheckInRequest;
import kh.gangnam.b2b.dto.work.request.attendance.CheckoutRequest;
import kh.gangnam.b2b.dto.work.response.attendance.DailyAttendanceResponse;
import kh.gangnam.b2b.dto.work.response.attendance.WeeklyAttendanceResponse;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

public interface AttendanceService {
    @Transactional
    void clockIn(Long employeeId, CheckInRequest request);


    DailyAttendanceResponse clockOut(Long employeeId, CheckoutRequest request);


    WeeklyAttendanceResponse getWeeklyAttendance(Long employeeId, LocalDate referenceDate);
}
