package kh.gangnam.b2b.dto.alarm;

import kh.gangnam.b2b.entity.alarm.Alarm;
import lombok.*;

import java.time.LocalDateTime;

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

    private String title;
    private Long  authorId;
    private String authorName;
    private LocalDateTime createdDate;

    private boolean isRead;


    public static AlarmDTO alarmDTO(Alarm alarm){ //변환용 메서드
        return AlarmDTO.builder()
                .alarmId(alarm.getAlarmId())
                .employeeId(alarm.getEmployee().getEmployeeId())
                .boardId(alarm.getBoard() != null ? alarm.getBoard().getBoardId() : null)

                .title(alarm.getBoard().getTitle())
                .authorId(alarm.getBoard().getAuthor().getEmployeeId())
                .authorName(alarm.getBoard().getAuthor().getName())
                .createdDate(alarm.getCreatedDate())

                .isRead(alarm.isRead())
                .build();
    }
}
