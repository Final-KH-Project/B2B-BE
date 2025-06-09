package kh.gangnam.b2b.dto.work.response.leave;

import kh.gangnam.b2b.entity.work.ApprovalStatus;
import kh.gangnam.b2b.entity.work.LeaveRequest;
import kh.gangnam.b2b.entity.work.WorkType;
import lombok.Data;
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

    public static LeaveRequestResponse fromEntity(LeaveRequest leaveRequest) {
        LeaveRequestResponse dto = new LeaveRequestResponse();
        dto.setLeaveRequestId(leaveRequest.getLeaveRequestId());
        dto.setEmployeeName(leaveRequest.getEmployee().getName());
        if (leaveRequest.getEmployee().getDept() != null) {
            dto.setDepartmentName(leaveRequest.getEmployee().getDept().getDeptName());
        } else {
            dto.setDepartmentName(null); // 또는 "미지정" 등 기본값
        }

        dto.setPosition(String.valueOf(leaveRequest.getEmployee().getPosition()));
        dto.setWorkType(leaveRequest.getWorkType().getKrName());
        dto.setStartDate(leaveRequest.getStartDate());
        dto.setEndDate(leaveRequest.getEndDate());
        dto.setStatus(leaveRequest.getStatus().name());
        dto.setReason(leaveRequest.getReason());
        dto.setRejectReason(leaveRequest.getRejectReason()); // 엔티티에 필드가 있으면
        dto.setApproverName(leaveRequest.getApprover().getName());
        return dto;
    }
}


