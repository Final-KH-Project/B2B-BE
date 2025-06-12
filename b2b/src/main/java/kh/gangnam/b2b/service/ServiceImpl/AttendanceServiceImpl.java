package kh.gangnam.b2b.service.ServiceImpl;

import jakarta.persistence.EntityNotFoundException;
import kh.gangnam.b2b.dto.work.request.attendance.CheckInRequest;
import kh.gangnam.b2b.dto.work.request.attendance.CheckoutRequest;
import kh.gangnam.b2b.dto.work.response.attendance.DailyAttendanceResponse;
import kh.gangnam.b2b.dto.work.response.attendance.WeeklyAttendanceResponse;
import kh.gangnam.b2b.entity.auth.Employee;
import kh.gangnam.b2b.entity.work.LeaveRequest;
import kh.gangnam.b2b.entity.work.WorkHistory;
import kh.gangnam.b2b.entity.work.WorkType;
import kh.gangnam.b2b.exception.ConflictException;
import kh.gangnam.b2b.exception.NotFoundException;
import kh.gangnam.b2b.repository.EmployeeRepository;
import kh.gangnam.b2b.repository.work.LeaveRequestRepository;
import kh.gangnam.b2b.repository.work.WorkHistoryRepository;
import kh.gangnam.b2b.service.AttendanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AttendanceServiceImpl implements AttendanceService {

    private final EmployeeRepository employeeRepository;
    private final WorkHistoryRepository workHistoryRepository;
    private final LeaveRequestRepository leaveRequestRepository;

    @Transactional
    @Override
    public void clockIn(Long employeeId, CheckInRequest request) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new NotFoundException("사원없음"));

        LocalDate today = LocalDate.now();
        boolean exists = workHistoryRepository.existsByEmployeeAndWorkDateAndWorkType(
                employee, today, WorkType.ATTENDANCE);

        if (exists) throw new ConflictException("이미 출근 기록이 존재합니다.");

        LocalDateTime startTime = (request != null && request.getStartTime() != null)
                ? request.getStartTime() : LocalDateTime.now();

        WorkHistory history = WorkHistory.builder()
                .employee(employee)
                .workType(WorkType.ATTENDANCE)
                .workDate(today)
                .startTime(startTime)
                .build();

        workHistoryRepository.save(history);
    }

    @Transactional
    @Override
    public DailyAttendanceResponse clockOut(Long employeeId, CheckoutRequest request) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new NotFoundException("사원 없음"));

        LocalDate workDate = (request != null && request.getWorkDate() != null)
                ? request.getWorkDate() : LocalDate.now();

        WorkHistory record = workHistoryRepository.findByEmployeeAndWorkDateAndWorkType(
                        employee, workDate, WorkType.ATTENDANCE)
                .orElseThrow(() -> new NotFoundException("출근 기록이 존재하지 않아요"));

        LocalDateTime endTime = (request != null && request.getEndTime() != null)
                ? request.getEndTime() : LocalDateTime.now();

        record.setEndTime(endTime);
        record.setNote(request != null ? request.getNote() : null);
        record.setWorkType(WorkType.LEAVE);

        record = workHistoryRepository.save(record);

        return DailyAttendanceResponse.from(record);
    }


    @Transactional(readOnly = true)
    @Override
    public WeeklyAttendanceResponse getWeeklyAttendance(Long employeeId, LocalDate referenceDate) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new NotFoundException("사원 없음"));

        LocalDate startOfWeek = referenceDate.with(DayOfWeek.MONDAY);
        LocalDate endOfWeek = startOfWeek.plusDays(6);

        List<DailyAttendanceResponse> dailyResponses = new ArrayList<>();

        for (LocalDate date = startOfWeek; !date.isAfter(endOfWeek); date = date.plusDays(1)) {

            // ✅ 여러 개의 출근 기록 중 첫 번째만 사용
            List<WorkHistory> workList = workHistoryRepository.findByEmployeeAndWorkDate(employee, date);
            WorkHistory work = workList.isEmpty() ? null : workList.get(0);

            List<LeaveRequest> leaves =
                    leaveRequestRepository.findApprovedLeaveForDate(employee, date);

            //출근한 경우
            WorkType type = (work != null) ? work.getWorkType() : null;
            for (LeaveRequest leave : leaves) {
                if (leave.getWorkType().name().contains("HALF_DAY") || work == null) {
                    type = leave.getWorkType();
                    break;
                }
            }

            //메모 (note)
            String note = (work != null && work.getNote() != null) ? work.getNote() :
                    (!leaves.isEmpty() ? leaves.get(0).getReason() : null);

            DailyAttendanceResponse daily = DailyAttendanceResponse.builder()
                    .workDate(date)
                    .workType(type)
                    .startTime(work != null ? work.getStartTime() : null)
                    .endTime(work != null ? work.getEndTime() : null)
                    .note(note)
                    .build();

            dailyResponses.add(daily);
        }

        return WeeklyAttendanceResponse.builder()
                .startDate(startOfWeek)
                .endDate(endOfWeek)
                .dailyRecords(dailyResponses)
                .build();
    }

}
