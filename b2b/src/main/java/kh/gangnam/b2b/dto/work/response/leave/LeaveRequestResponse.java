package kh.gangnam.b2b.dto.work.response.leave;

import kh.gangnam.b2b.entity.work.LeaveRequest;
import kh.gangnam.b2b.entity.work.WorkType;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Data
public class LeaveRequestResponse {
    private Long leaveRequestId;
    private String employeeName;
    private WorkType workType;
    private LocalDate startDate;
    private LocalDate endDate;
    //private ApprovalStatus status;
    private String reason;

    public static LeaveRequestResponse from(LeaveRequest req) {
        LeaveRequestResponse res = new LeaveRequestResponse();
        res.setLeaveRequestId(req.getLeaveRequestId());
        res.setEmployeeName(req.getEmployee().getName());
        res.setStartDate(req.getStartDate());
        res.setEndDate(req.getEndDate());
        res.setWorkType(req.getWorkType());
        //res.setStatus(req.getStatus());
        res.setReason(req.getReason());
        return res;
    }
}
