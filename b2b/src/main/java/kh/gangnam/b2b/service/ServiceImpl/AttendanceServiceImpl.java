
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

    //  출근 처리 로직
    @Transactional
    @Override
    public void clockIn(Long employeeId, ClockInRequest request) {
        // 사원 조회
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new EntityNotFoundException("사원없음"));

        // 오늘 날짜 기준 출근 기록 있는지 확인
        LocalDate today = LocalDate.now();
        boolean exists = workHistoryRepository.existsByEmployeeAndWorkDateAndWorkType(
                employee, today, WorkType.ATTENDANCE);

        // 요청 값이 없으면 현재 시간으로 설정
        LocalDateTime startTime = (request != null && request.getStartTime() != null)
                ? request.getStartTime() : LocalDateTime.now();

        if (exists) throw new IllegalStateException("이미 출근 기록이 존재합니다.");

        // 출근 이력 생성 및 저장
        WorkHistory history = WorkHistory.builder()
                .employee(employee)
                .workType(WorkType.ATTENDANCE)
                .workDate(today)
                .startTime(startTime)
                .build();

        workHistoryRepository.save(history);
    }

    //  퇴근 처리 로직
    @Transactional
    @Override
    public void clockOut(Long employeeId, ClockOutRequest request) {
        // 사원 조회
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new EntityNotFoundException("사원 없음"));

        // 퇴근할 날짜 설정 (요청 없으면 오늘)
        LocalDate workDate = (request != null && request.getWorkDate() != null)
                ? request.getWorkDate() : LocalDate.now();

        // 해당 날짜 출근 기록이 있는지 확인
        WorkHistory record = workHistoryRepository.findByEmployeeAndWorkDateAndWorkType(
                        employee, workDate, WorkType.ATTENDANCE)
                .orElseThrow(() -> new RuntimeException("출근 기록이 존재하지 않아요우~"));

        // 퇴근 시간 설정
        LocalDateTime endTime = (request != null && request.getEndTime() != null)
                ? request.getEndTime() : LocalDateTime.now();

        // 퇴근 처리 및 저장
        record.setEndTime(endTime);
        record.setNote(request != null ? request.getNote() : null);
        record.setWorkType(WorkType.LEAVE);

        workHistoryRepository.save(record);
    }

    //  반차/외근 등 일반 근무유형 신청 처리 로직
    @Transactional
    @Override
    public void applyWork(Long employeeId, WorkApplyRequest request) {
        throw new UnsupportedOperationException("❌ 반차/출장/연차 신청은 leave_request API로만 처리됩니다.");
//        // 사원 조회
//        Employee employee = employeeRepository.findById(employeeId)
//                .orElseThrow(() -> new EntityNotFoundException("사원없음"));
//
//        // 필수값 체크
//        if (request.getWorkDate() == null || request.getWorkType() == null) {
//            throw new IllegalArgumentException("신청 날짜와 근무 유형은 필수 입니다");
//        }
//
//        // 중복 신청 방지
//        boolean exists = workHistoryRepository.existsByEmployeeAndWorkDateAndWorkType(
//                employee, request.getWorkDate(), request.getWorkType());
//
//        if (exists) {
//            throw new IllegalStateException("이미 해당 날짜에 같은 유형의 근무기록이 존재합니다");
//        }
//
//        // 근무기록 저장
//        WorkHistory history = WorkHistory.builder()
//                .employee(employee)
//                .workDate(request.getWorkDate())
//                .workType(request.getWorkType())
//                .note(request.getNote())
//                .build();
//
//        workHistoryRepository.save(history);
    }

    //  주간 근태 기록 조회
    @Transactional(readOnly = true)
    @Override
    public WeeklyAttendanceResponse getWeeklyAttendance(Long employeeId, LocalDate referenceDate) {
        // 1. 사원 조회
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new EntityNotFoundException("사원 없음"));

        // 2. 기준 날짜를 포함하는 주의 월요일 ~ 일요일 계산
        LocalDate startOfWeek = referenceDate.with(DayOfWeek.MONDAY);
        LocalDate endOfWeek = startOfWeek.plusDays(6);

        // 3. 주간 응답용 리스트 준비
        List<DailyAttendanceResponse> dailyResponses = new ArrayList<>();

        // 4. 월~일까지 하루씩 반복
        for (LocalDate date = startOfWeek; !date.isAfter(endOfWeek); date = date.plusDays(1)) {
            // 4-1. 해당 날짜의 출근 기록 조회
            WorkHistory work = workHistoryRepository.findByEmployeeAndWorkDate(employee, date);

            // 4-2. 결과 조립 (없는 경우 null로 대체)
            DailyAttendanceResponse daily = DailyAttendanceResponse.builder()
                    .workDate(date)
                    .workType(work != null ? work.getWorkType() : null)
                    .startTime(work != null ? work.getStartTime() : null)
                    .endTime(work != null ? work.getEndTime() : null)
                    .note(work != null ? work.getNote() : null)
                    .build();

            dailyResponses.add(daily);
        }

        // 5. 최종 응답 생성 및 반환
        return WeeklyAttendanceResponse.builder()
                .startDate(startOfWeek)
                .endDate(endOfWeek)
                .dailyRecords(dailyResponses)
                .build();
    }

    //  연차/반차/출장 신청 처리 - leave_request 테이블에 저장
    @Transactional
    @Override
    public void applyLeave(Long employeeId, LeaveRequest dto) {
        // 신청자 조회
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new EntityNotFoundException("신청자 없음"));

        // 결재자 조회
        Employee approver = employeeRepository.findById(dto.getApproverId())
                .orElseThrow(() -> new EntityNotFoundException("결재자 없음"));

        // LeaveRequest 엔티티 생성 및 값 설정
        kh.gangnam.b2b.entity.work.LeaveRequest request = new kh.gangnam.b2b.entity.work.LeaveRequest();
        request.setEmployee(employee);
        request.setApprover(approver);
        request.setWorkType(dto.getWorkType());
        request.setStartDate(dto.getStartDate());
        request.setEndDate(dto.getEndDate());
        request.setReason(dto.getReason());
        request.setStatus(ApprovalStatus.PENDING); // 기본 상태: 대기

        // leave_request 테이블에 저장
        leaveRequestRepository.save(request);
    }
}
