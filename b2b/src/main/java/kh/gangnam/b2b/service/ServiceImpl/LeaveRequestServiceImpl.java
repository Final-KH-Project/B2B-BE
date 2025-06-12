package kh.gangnam.b2b.service.ServiceImpl;

import jakarta.persistence.EntityNotFoundException;
import kh.gangnam.b2b.dto.work.request.leave.LeaveRequestRequest;
import kh.gangnam.b2b.dto.work.response.leave.LeaveRequestResponse;
import kh.gangnam.b2b.dto.work.response.leave.LeaveStatusResponse;
import kh.gangnam.b2b.entity.auth.Employee;
import kh.gangnam.b2b.entity.work.ApprovalStatus;
import kh.gangnam.b2b.entity.work.LeaveRequest;
import kh.gangnam.b2b.entity.work.WorkHistory;
import kh.gangnam.b2b.entity.work.WorkType;
import kh.gangnam.b2b.exception.InvalidRequestException;
import kh.gangnam.b2b.exception.NotFoundException;
import kh.gangnam.b2b.repository.EmployeeRepository;
import kh.gangnam.b2b.repository.work.LeaveRequestRepository;
import kh.gangnam.b2b.repository.work.WorkHistoryRepository;
import kh.gangnam.b2b.service.LeaveRequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LeaveRequestServiceImpl implements LeaveRequestService {

    private final EmployeeRepository employeeRepository;
    private final LeaveRequestRepository leaveRequestRepository;
    private final WorkHistoryRepository workHistoryRepository;

    //연차 신청 처리 (leave_request 테이블에 저장됨)
    @Transactional
    @Override
    public void applyLeave(Long employeeId, LeaveRequestRequest dto) {

        // 신청자(로그인 사용자)조회, 없는 경우는 예외처리
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new NotFoundException("신청자 없음"));

        LocalDate startDate = dto.getStartDate();
        LocalDate endDate = dto.getEndDate();
        WorkType requestedType = dto.getWorkType();

        // 신청한 날짜 범위에 중복 연차가 있는지 검증
        validateDuplicateLeave(employee, requestedType, startDate, endDate);

        //출근 기록이 있을경우 연차/출장 신청 불가
        if (requestedType == WorkType.VACATION || requestedType == WorkType.BUSINESS_TRIP) {
            boolean hasWorkHistory = workHistoryRepository.existsByEmployeeAndWorkDateBetween(
                    employee, startDate, endDate);
            if (hasWorkHistory) {
                throw new InvalidRequestException("해당 기간에 이미 출근 기록이 있어 " +
                        requestedType.getDisplayName() + " 신청이 불가능합니다.");
            }
        }
        // LeaveRequest 엔티티 생성 후 저장
        LeaveRequest request = new LeaveRequest();
        request.setEmployee(employee);
        request.setWorkType(dto.getWorkType());
        request.setStartDate(dto.getStartDate());
        request.setEndDate(dto.getEndDate());
        request.setReason(dto.getReason());
        request.setStatus(ApprovalStatus.PENDING);//처음엔 무조건 대기(PENDING)상태

        // DB 저장
        leaveRequestRepository.save(request);
    }
    // ✅ 중복 근무유형 신청 검사 (반차 vs 전일, 전일 vs 전일)
    private void validateDuplicateLeave(Employee employee, WorkType requestedType, LocalDate startDate, LocalDate endDate) {
        //해당 기간에 이미 승인된 연차목록 조회
        List<LeaveRequest> overlapped = leaveRequestRepository.findApprovedInRange(employee, startDate, endDate);

        boolean requestedIsFull = isFullDay(requestedType); //신청한것이 전일인가?
        boolean requestedIsHalf = isHalfDay(requestedType); //신청한것이 반차인가?

        for (LeaveRequest exist : overlapped) {
            WorkType existType = exist.getWorkType();
            boolean existingIsFull = isFullDay(existType); //기존것이 전일인가?
            boolean existingIsHalf = isHalfDay(existType); //기존것도 반차인가?
            //반차 신청인데 기존에 전일 연차가 있음 -> 예외
            if (requestedIsHalf && existingIsFull) {
                throwConflictException(startDate, endDate, exist, "전일", true);
            }
            //전일 신청인데 기존에 전일 연차 있음 -> 예외
            if (requestedIsFull && existingIsFull) {
                throwConflictException(startDate, endDate, exist, "전일", false);
            }
            //전일 신청인데 기존에 반차 있음 -> 예외
            if (requestedIsFull && existingIsHalf) {
                throwConflictException(startDate, endDate, exist, "반차", false);
            }
        }
    }
    // 전일 연차 판별 함수
    private boolean isFullDay(WorkType type) {
        return type == WorkType.VACATION || type == WorkType.BUSINESS_TRIP;
    }
    // 반차 판별 함수
    private boolean isHalfDay(WorkType type) {
        return type == WorkType.AM_HALF_DAY || type == WorkType.PM_HALF_DAY;
    }
    //연차 중복 예외 메시지 생성 및 던짐
    private void throwConflictException(LocalDate start, LocalDate end, LeaveRequest exist, String conflictType, boolean isHalfRequest) {
        LocalDate conflictDate = start.datesUntil(end.plusDays(1))
                .filter(d -> !d.isBefore(exist.getStartDate()) && !d.isAfter(exist.getEndDate()))
                .findFirst()
                .orElse(exist.getStartDate());
        //기존 연차의 유형을 한글로 변환
        String typeKor = switch (exist.getWorkType()) {
            case VACATION -> "연차";
            case BUSINESS_TRIP -> "출장";
            case AM_HALF_DAY -> "오전 반차";
            case PM_HALF_DAY -> "오후 반차";
            default -> "근무유형";
        };
        // 충돌 유형별 메시지 처리
        String msg = switch (conflictType) {
            case "전일" -> isHalfRequest
                    ? conflictDate + "에는 이미 전일 " + typeKor + "(" + exist.getWorkType() + ")이 등록되어 있어 반차 신청이 어렵습니다. 다른 날짜를 선택해 주세요."
                    : conflictDate + "에는 이미 전일 " + typeKor + "(" + exist.getWorkType() + ")이 등록되어 있어 중복 신청이 불가능합니다.";
            case "반차" -> conflictDate + "에는 이미 " + typeKor + " 신청이 등록되어 있어 전일 연차 또는 출장 신청이 불가합니다.";
            default -> "근무유형 중복 신청이 존재합니다.";
        };
        //최종 예외 발생
        throw new InvalidRequestException(msg);
    }
    /*
    *관리자가 승인처리 -> Approve상태로 변경 + WorkHistory에 자동 insert
     */
    @Transactional
    @Override
    public void approveLeave(Long requestId, Long approvedId){
        //요청 내역 조회
        LeaveRequest request = leaveRequestRepository.findById(requestId)
                .orElseThrow(()-> new NotFoundException("연차 신청 내역 없음"));

        // 상태를 APPROVED 로 변경
        request.setStatus(ApprovalStatus.APPROVED);

        //해당 연차 기간 전체에 대해 Work_History 자동 기록
        request.getStartDate().datesUntil(request.getEndDate().plusDays(1)).forEach(date->{
            WorkHistory history = WorkHistory.builder()
                    .employee(request.getEmployee())
                    .workDate(date)
                    .workType(request.getWorkType())
                    .note("승인된 연차 자동 기록")
                    .build();
            workHistoryRepository.save(history);
        });
    }

    //연차 현황 조회 (10일 기준으로)
    @Transactional(readOnly = true)
    @Override
    public LeaveStatusResponse getLeaveStatus(Long employeeId) {
        // 사용자 조회
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new NotFoundException("사원 없음"));

        // 승인된 휴가만 조회 (PEDING, REJECTED 제외)
        List<LeaveRequest> approvedLeaves = leaveRequestRepository
                .findByEmployeeAndStatus(employee, ApprovalStatus.APPROVED);

        // 사용한 연차 일수 계산
        double usedLeave = 0.0;
        for (LeaveRequest leave : approvedLeaves) {
            switch (leave.getWorkType()) {
                case VACATION -> {
                    long days = leave.getStartDate().datesUntil(leave.getEndDate().plusDays(1)).count();
                    usedLeave += days;
                }
                case AM_HALF_DAY, PM_HALF_DAY -> usedLeave += 0.5;
            }
        }
        // 잔여연차 및 퍼센트 계산
        double totalLeave = 10.0;
        double remainingLeave = totalLeave - usedLeave;
        double remainingPercentage = (remainingLeave / totalLeave) * 100.0;

        // 응답 DTO로 반환
        return LeaveStatusResponse.builder()
                .totalLeave(totalLeave)
                .usedLeave(usedLeave)
                .remainingLeave(remainingLeave)
                .remainingPercentage(remainingPercentage)
                .build();

    }
    //연차 신청 내역 조회
    @Override
    @Transactional(readOnly = true)
    public List<LeaveRequestResponse> getMyRequests(Long employeeId){
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(()-> new NotFoundException("사원 없음"));

    List<LeaveRequest> list = leaveRequestRepository.findByEmployee(employee);
    return LeaveRequestResponse.fromList(list);
    }
}

