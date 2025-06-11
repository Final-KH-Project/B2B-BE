package kh.gangnam.b2b.dto.work.response.attendance;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Builder
public class WeeklyAttendanceResponse {
    private LocalDate startDate;
    private LocalDate endDate;
    private List<DailyAttendanceResponse> dailyRecords;
}
