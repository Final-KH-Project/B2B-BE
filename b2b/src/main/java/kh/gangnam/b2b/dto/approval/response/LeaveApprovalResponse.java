package kh.gangnam.b2b.dto.approval.response;

import kh.gangnam.b2b.entity.work.ApprovalStatus;
import kh.gangnam.b2b.entity.work.LeaveRequest;
import kh.gangnam.b2b.entity.work.WorkType;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class LeaveApprovalResponse {
    private Long leaveRequestId;
    private String employeeName;
    private String departmentName;
    private WorkType workType;
    private LocalDate startDate;
    private LocalDate endDate;
    private ApprovalStatus status;
    private String reason;
    private String rejectReason;   // 엔티티에 저장하지 않고 응답에서만 사용
    private String approverName;

    // 엔티티 → DTO 변환 메서드 (rejectReason은 별도 세팅)
    public static LeaveApprovalResponse fromEntity(LeaveRequest entity) {
        LeaveApprovalResponse dto = new LeaveApprovalResponse();
        dto.setLeaveRequestId(entity.getLeaveRequestId());
        dto.setEmployeeName(entity.getEmployee().getName());
        dto.setDepartmentName(entity.getEmployee().getDepartment());
        dto.setWorkType(entity.getWorkType());
        dto.setStartDate(entity.getStartDate());
        dto.setEndDate(entity.getEndDate());
        dto.setStatus(entity.getStatus());
        dto.setReason(entity.getReason());
        dto.setApproverName(entity.getApprover().getName());
        // dto.setRejectReason(null); // 여기서는 세팅하지 않음
        return dto;
    }
}
