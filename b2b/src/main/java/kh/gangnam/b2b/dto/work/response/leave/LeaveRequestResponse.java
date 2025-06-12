package kh.gangnam.b2b.dto.work.response.leave;

import kh.gangnam.b2b.entity.work.LeaveRequestEntity;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
@Getter
@Setter
public class LeaveRequestResponse {
    private Long leaveRequestId;
    private String employeeName;
    private String departmentName;
    private String position;
    private String workType;
    private LocalDate startDate;
    private LocalDate endDate;
    private String status;
    private String reason;
    private String rejectReason;
    private String approverName;

    public static LeaveRequestResponse fromEntity(LeaveRequestEntity leaveRequestEntity) {
        LeaveRequestResponse dto = new LeaveRequestResponse();
        dto.setLeaveRequestId(leaveRequestEntity.getLeaveRequestId());
        dto.setEmployeeName(leaveRequestEntity.getEmployee().getName());
        if (leaveRequestEntity.getEmployee().getDept() != null) {
            dto.setDepartmentName(leaveRequestEntity.getEmployee().getDept().getDeptName());
        } else {
            dto.setDepartmentName(null); // 또는 "미지정" 등 기본값
        }

        dto.setPosition(String.valueOf(leaveRequestEntity.getEmployee().getPosition()));
        dto.setWorkType(leaveRequestEntity.getWorkType().getKrName());
        dto.setStartDate(leaveRequestEntity.getStartDate());
        dto.setEndDate(leaveRequestEntity.getEndDate());
        dto.setStatus(leaveRequestEntity.getStatus().name());
        dto.setReason(leaveRequestEntity.getReason());
        dto.setRejectReason(leaveRequestEntity.getRejectReason()); // 엔티티에 필드가 있으면
        dto.setApproverName(leaveRequestEntity.getApprover().getName());
        return dto;
    }
}


