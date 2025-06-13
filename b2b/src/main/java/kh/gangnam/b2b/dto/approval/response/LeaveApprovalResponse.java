package kh.gangnam.b2b.dto.approval.response;

import kh.gangnam.b2b.entity.work.ApprovalStatus;
import kh.gangnam.b2b.entity.work.LeaveRequestEntity;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class LeaveApprovalResponse {
    private Long leaveRequestId;
    private String employeeName;
    private String departmentName;
    private String position;
    private String workType;
    private LocalDate startDate;
    private LocalDate endDate;
    private ApprovalStatus status;
    private String reason;
    private String rejectReason;
    private String approverName;

    // 엔티티 → DTO 변환 메서드 (rejectReason은 별도 세팅)
    public static LeaveApprovalResponse fromEntity(LeaveRequestEntity entity) {
        LeaveApprovalResponse dto = new LeaveApprovalResponse();
        dto.setLeaveRequestId(entity.getLeaveRequestId());
        dto.setEmployeeName(entity.getEmployee().getName());
        dto.setDepartmentName((entity.getEmployee().getDept() != null) ? entity.getEmployee().getDept().getDeptName() : "부서없음");
        dto.setPosition(entity.getEmployee().getPosition() != null ? entity.getEmployee().getPosition().getKrName() : null);
        dto.setWorkType(entity.getWorkType() != null ? entity.getWorkType().getKrName() : null);
        dto.setStartDate(entity.getStartDate());
        dto.setEndDate(entity.getEndDate());
        dto.setStatus(entity.getStatus());
        dto.setReason(entity.getReason());
        dto.setApproverName(entity.getApprover().getName());
        dto.setRejectReason(entity.getRejectReason());
        return dto;
    }
}
