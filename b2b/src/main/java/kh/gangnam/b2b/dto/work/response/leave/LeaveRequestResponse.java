package kh.gangnam.b2b.dto.work.response.leave;

import kh.gangnam.b2b.entity.work.LeaveRequestEntity;
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
    private String workType;
    private LocalDate startDate;
    private LocalDate endDate;
    private String status;
    private String reason;

    public static LeaveRequestResponse from(LeaveRequestEntity req) {
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
                .map(LeaveRequestResponse::from)
                .collect(Collectors.toList());
    }
}


