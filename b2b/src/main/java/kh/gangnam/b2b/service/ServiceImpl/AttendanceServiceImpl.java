package kh.gangnam.b2b.service.ServiceImpl;

import jakarta.persistence.EntityNotFoundException;
import kh.gangnam.b2b.dto.work.request.leave.LeaveRequest;
import kh.gangnam.b2b.dto.work.request.attendance.ClockInRequest;
import kh.gangnam.b2b.dto.work.request.attendance.ClockOutRequest;
import kh.gangnam.b2b.dto.work.request.leave.WorkApplyRequest;
import kh.gangnam.b2b.dto.work.response.attendance.DailyAttendanceResponse;
import kh.gangnam.b2b.dto.work.response.attendance.WeeklyAttendanceResponse;
import kh.gangnam.b2b.entity.auth.Employee;
import kh.gangnam.b2b.entity.work.ApprovalStatus;
import kh.gangnam.b2b.entity.work.LeaveRequestEntity;
import kh.gangnam.b2b.entity.work.WorkHistory;
import kh.gangnam.b2b.entity.work.WorkType;
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
    public void clockIn(Long employeeId, ClockInRequest request) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new EntityNotFoundException("사원없음"));

        LocalDate today = LocalDate.now();
        boolean exists = workHistoryRepository.existsByEmployeeAndWorkDateAndWorkType(
                employee, today, WorkType.ATTENDANCE);

        LocalDateTime startTime = (request != null && request.getStartTime() != null)
                ? request.getStartTime() : LocalDateTime.now();

        if (exists) throw new IllegalStateException("이미 출근 기록이 존재합니다.");

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
    public void clockOut(Long employeeId, ClockOutRequest request) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new EntityNotFoundException("사원 없음"));

        LocalDate workDate = (request != null && request.getWorkDate() != null)
                ? request.getWorkDate() : LocalDate.now();

        WorkHistory record = workHistoryRepository.findByEmployeeAndWorkDateAndWorkType(
                        employee, workDate, WorkType.ATTENDANCE)
                .orElseThrow(() -> new RuntimeException("출근 기록이 존재하지 않아요우~"));

        LocalDateTime endTime = (request != null && request.getEndTime() != null)
                ? request.getEndTime() : LocalDateTime.now();

        record.setEndTime(endTime);
        record.setNote(request != null ? request.getNote() : null);
        record.setWorkType(WorkType.LEAVE);

        workHistoryRepository.save(record);
    }

    @Transactional
    @Override
    public void applyWork(Long employeeId, WorkApplyRequest request) {
        throw new UnsupportedOperationException("❌ 반차/출장/연차 신청은 leave_request API로만 처리됩니다.");
    }

    @Transactional(readOnly = true)
    @Override
    public WeeklyAttendanceResponse getWeeklyAttendance(Long employeeId, LocalDate referenceDate) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new EntityNotFoundException("사원 없음"));

        LocalDate startOfWeek = referenceDate.with(DayOfWeek.MONDAY);
        LocalDate endOfWeek = startOfWeek.plusDays(6);

        List<DailyAttendanceResponse> dailyResponses = new ArrayList<>();

        for (LocalDate date = startOfWeek; !date.isAfter(endOfWeek); date = date.plusDays(1)) {

            // ✅ 여러 개의 출근 기록 중 첫 번째만 사용
            List<WorkHistory> workList = workHistoryRepository.findByEmployeeAndWorkDate(employee, date);
            WorkHistory work = workList.isEmpty() ? null : workList.get(0);

            List<LeaveRequestEntity> leaves =
                    leaveRequestRepository.findApprovedLeaveForDate(employee, date);

            WorkType type = null;
            if (work != null) {
                type = work.getWorkType();
            }

            for (LeaveRequestEntity leave : leaves) {
                if (leave.getWorkType().name().contains("HALF_DAY")) {
                    type = leave.getWorkType();
                    break;
                }
                if (work == null) {
                    type = leave.getWorkType();
                    break;
                }
            }

            String note = null;
            if (work != null && work.getNote() != null) {
                note = work.getNote();
            } else if (!leaves.isEmpty()) {
                note = leaves.get(0).getReason();
            }

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

    @Transactional
    @Override
    public void applyLeave(Long employeeId, LeaveRequest dto) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new EntityNotFoundException("신청자 없음"));

        Employee approver = employeeRepository.findById(dto.getApproverId())
                .orElseThrow(() -> new EntityNotFoundException("결재자 없음"));

        LeaveRequestEntity request = new LeaveRequestEntity();
        request.setEmployee(employee);
        request.setApprover(approver);
        request.setWorkType(dto.getWorkType());
        request.setStartDate(dto.getStartDate());
        request.setEndDate(dto.getEndDate());
        request.setReason(dto.getReason());
        request.setStatus(ApprovalStatus.PENDING);

        leaveRequestRepository.save(request);
    }
}
