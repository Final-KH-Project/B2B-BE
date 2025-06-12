package kh.gangnam.b2b.dto.work.response.attendance;

import kh.gangnam.b2b.entity.work.WorkHistory;
import kh.gangnam.b2b.entity.work.WorkType;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Builder
public class DailyAttendanceResponse {
    private LocalDate workDate;
    private WorkType workType;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String note;

    public static DailyAttendanceResponse from(WorkHistory entity){
        return DailyAttendanceResponse.builder()
                .workDate(entity.getWorkDate())
                .workType(entity.getWorkType())
                .startTime(entity.getStartTime())
                .endTime(entity.getEndTime())
                .note(entity.getNote())
                .build();
    }
}
