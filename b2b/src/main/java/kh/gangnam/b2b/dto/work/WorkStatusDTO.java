package kh.gangnam.b2b.dto.work;

import kh.gangnam.b2b.entity.work.WorkType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@Builder
public class WorkStatusDTO {
    //연차 현황 DTO
    private LocalDate date; // 해당 일자
    private WorkType workType; // 근무 유형
    private LocalDateTime startTime; // 출근 시간
    private LocalDateTime endtime; // 퇴근 시간
    private String note; //비고
}
