package kh.gangnam.b2b.dto.alarm.response;

import lombok.*;

import java.time.LocalDateTime;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReadAlarm {

    // 알람 읽기 응답 DTO
    // Alarm 엔티티 필드가 존재해야 함
    private Long alarmId;
    private Long employeeId;
    private boolean isRead;
    private LocalDateTime createdDate;



}
