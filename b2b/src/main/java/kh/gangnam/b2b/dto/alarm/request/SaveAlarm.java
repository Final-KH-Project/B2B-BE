package kh.gangnam.b2b.dto.alarm.request;

import lombok.*;
import software.amazon.awssdk.annotations.NotNull;

@Data
@Builder
@NoArgsConstructor //생략 가능하나, json 파싱 오류 방지용
@AllArgsConstructor
public class SaveAlarm {

    // 알람 저장 요청 DTO
    // 알람 엔티티 필드가 존재해야 함
    private Long employeeId;
    private Long boardId;


} //DB에서 자동 생성 되므로 alarmId 필드는 필요치 않음
