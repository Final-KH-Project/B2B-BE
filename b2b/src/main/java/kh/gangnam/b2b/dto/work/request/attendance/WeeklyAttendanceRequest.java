package kh.gangnam.b2b.dto.work.request.attendance;

import lombok.Getter;

import java.time.LocalDate;

@Getter
public class WeeklyAttendanceRequest {
    //주 단위 근태
    private LocalDate referenceDate; //기준 날짜 (이 날짜가 속한 주의 근태 조회)
}
