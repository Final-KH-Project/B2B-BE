package kh.gangnam.b2b.dto.work.request.leave;

import kh.gangnam.b2b.entity.work.WorkType;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class LeaveRequest {
    private Long approverId;
    private WorkType workType;       // 연차 유형
    private LocalDate startDate;     // 시작 날짜
    private LocalDate endDate;       // 종료 날짜
    private String reason;           // 신청 사유
}
