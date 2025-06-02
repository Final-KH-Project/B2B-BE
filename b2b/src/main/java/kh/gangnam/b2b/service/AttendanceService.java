package kh.gangnam.b2b.service;

import kh.gangnam.b2b.dto.work.LeaveRequestDTO;
import kh.gangnam.b2b.dto.work.request.attendance.ClockInRequest;
import kh.gangnam.b2b.dto.work.request.attendance.ClockOutRequest;
import kh.gangnam.b2b.dto.work.response.attendance.WeeklyAttendanceResponse;
import kh.gangnam.b2b.dto.work.request.leave.WorkApplyRequest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

public interface AttendanceService {
    @Transactional
    void clockIn(Long employeeId, ClockInRequest request);

    @Transactional
    void clockOut(Long employeeId, ClockOutRequest request);

    //연차 신청
    @Transactional
    void applyWork(Long employeeId, WorkApplyRequest request);

    WeeklyAttendanceResponse getWeeklyAttendance(Long employeeId, LocalDate referenceDate);

    @Transactional
    void applyLeave(Long employeeId, LeaveRequestDTO dto);
}
