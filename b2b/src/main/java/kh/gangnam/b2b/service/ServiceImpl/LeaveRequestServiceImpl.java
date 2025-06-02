package kh.gangnam.b2b.service.ServiceImpl;

import jakarta.persistence.EntityNotFoundException;
import kh.gangnam.b2b.dto.work.LeaveRequestDTO;
import kh.gangnam.b2b.dto.work.response.leave.LeaveStatusResponse;
import kh.gangnam.b2b.entity.auth.Employee;
import kh.gangnam.b2b.entity.work.ApprovalStatus;
import kh.gangnam.b2b.entity.work.LeaveRequest;
import kh.gangnam.b2b.entity.work.WorkHistory;
import kh.gangnam.b2b.repository.EmployeeRepository;
import kh.gangnam.b2b.repository.work.LeaveRequestRepository;
import kh.gangnam.b2b.repository.work.WorkHistoryRepository;
import kh.gangnam.b2b.service.LeaveRequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    public void applyLeave(Long employeeId, LeaveRequestDTO dto) {

        // 신청자(로그인 사용자)조회
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new EntityNotFoundException("신청자 없음"));

        // 결재자 조회
        Employee approver = employeeRepository.findById(dto.getApproverId())
                .orElseThrow(() -> new EntityNotFoundException("결재자 없음"));

        // 연차 요청 생성
        LeaveRequest request = new LeaveRequest();
        request.setEmployee(employee);
        request.setApprover(approver);
        request.setWorkType(dto.getWorkType());
        request.setStartDate(dto.getStartDate());
        request.setEndDate(dto.getEndDate());
        request.setReason(dto.getReason());
        request.setStatus(ApprovalStatus.PENDING);

        // DB 저장
        leaveRequestRepository.save(request);
    }
    /*
    *관리자가 승인처리 -> Approve상태로 변경 + WorkHistory에 자동 insert
     */
    @Transactional
    @Override
    public void approveLeave(Long requestId, Long approvedId){
        LeaveRequest request = leaveRequestRepository.findById(requestId)
                .orElseThrow(()-> new EntityNotFoundException("연차 신청 내역 없음"));
        //결재자 권한 확인
        if (!request.getApprover().getEmployeeId().equals(approvedId)){
            throw new SecurityException("결재 권한 없음");
        }
        //상태변경
        request.setStatus(ApprovalStatus.APPROVED);

        //Work_History 자동 기록
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
                .orElseThrow(() -> new EntityNotFoundException("사원 없음"));

        // 승인된 휴가만 조회 (PEDING, REJECTED 제외)
        List<LeaveRequest> approvedLeaves = leaveRequestRepository
                .findByEmployeeAndStatus(employee, ApprovalStatus.APPROVED);

        // 사용한 연차 일수 계산
        double usedLeave = 0.0;
        for (LeaveRequest leave : approvedLeaves) {
            switch (leave.getWorkType()) {
                case VACATION:
                case BUSINESS_TRIP:
                    //시작일~종료일(포함)까지의 일 수 계산
                    long days = leave.getStartDate().datesUntil(leave.getEndDate().plusDays(1)).count();
                    usedLeave += days;
                    break;
                case AM_HALF_DAY:
                case PM_HALF_DAY:
                    usedLeave += 0.5;
                    break;
                default:
                    break;
            }
        }
        // 잔여연차 및 퍼센트 계산
        double totalLeave = 10.0;
        double remainingLeave = totalLeave - usedLeave;
        double remainingPercentage = (remainingLeave / totalLeave) * 100.0;

        // DTO로 반환
        return LeaveStatusResponse.builder()
                .totalLeave(totalLeave)
                .usedLeave(usedLeave)
                .remainingLeave(remainingLeave)
                .remainingPercentage(remainingPercentage)
                .build();

    }
    @Override
    @Transactional(readOnly = true)
    public List<LeaveRequest> getMyRequests(Long employeeId){
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(()-> new EntityNotFoundException("사원 없음"));

        return leaveRequestRepository.findByEmployee(employee);
    }
}
