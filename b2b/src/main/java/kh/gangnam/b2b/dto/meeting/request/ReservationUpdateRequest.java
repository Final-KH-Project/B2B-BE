package kh.gangnam.b2b.dto.meeting.request;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReservationUpdateRequest {

    private Long reservationId;
    // 수정 가능한 항목만 포함
    private String topic;                // 주제 (nullable)
    private String purpose;              // 목적 (nullable)
    private String content;              // 내용 (nullable)
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private List<Long> participantIds;   // 참여자 ID 리스트 (nullable)
}