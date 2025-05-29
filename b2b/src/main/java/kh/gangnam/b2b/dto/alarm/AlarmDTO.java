package kh.gangnam.b2b.dto.alarm;

import kh.gangnam.b2b.entity.alarm.Alarm;
import lombok.*;

//to Dto - mapping
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AlarmDTO {
    private Long alarmId;
    private Long employeeId;
    private Long boardId;
    private boolean isRead;


    public static AlarmDTO alarmDTO(Alarm alarm){ //변환용 메서드
        return AlarmDTO.builder()
                .alarmId(alarm.getAlarmId())
                .employeeId(alarm.getEmployee().getEmployeeId())
                .boardId(alarm.getBoard() != null ? alarm.getBoard().getBoardId() : null)
                .isRead(alarm.isRead())
                .build();
    }
}
