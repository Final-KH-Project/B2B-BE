package kh.gangnam.b2b.dto.work.request.attendance;

import kh.gangnam.b2b.entity.work.WorkHistory;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CheckoutRequest {
    //퇴근DTO
    private LocalDateTime endTime;   // 선택: 퇴근 시간 (없으면 now)
    private LocalDate workDate;      // 선택: 퇴근 처리할 날짜 (기본은 오늘)
    private String note;             // 선택: 조퇴 사유 등

    public static CheckoutRequest fromEntity(WorkHistory entity) {
        return CheckoutRequest.builder()
                .endTime(entity.getEndTime())
                .workDate(entity.getWorkDate())
                .note(entity.getNote())
                .build();
    }
}
