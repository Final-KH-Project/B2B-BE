package kh.gangnam.b2b.dto.work.response.leave;

import kh.gangnam.b2b.entity.work.ApprovalStatus;
import kh.gangnam.b2b.entity.work.LeaveRequestEntity;
import kh.gangnam.b2b.entity.work.WorkType;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
@Data
public class LeaveRequestResponse {
    private Long leaveRequestId;
    private String employeeName;
    private WorkType workType;
    private LocalDate startDate;
    private LocalDate endDate;
    private ApprovalStatus status;
    private String reason;

    public static LeaveRequestResponse fromEntity(LeaveRequestEntity req) {
        return LeaveRequestResponse.builder()
                .leaveRequestId(req.getLeaveRequestId())
                .employeeName(req.getEmployee().getName())
                .workType(req.getWorkType())
                .startDate(req.getStartDate())
                .endDate(req.getEndDate())
                .status(req.getStatus())
                .reason(req.getReason())
                .build();

    }
    public static List<LeaveRequestResponse> fromList(List<LeaveRequestEntity> requestList){
        return requestList.stream()
                .map(LeaveRequestResponse::fromEntity)
                .collect(Collectors.toList());
    }
}


