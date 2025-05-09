package kh.gangnam.b2b.dto.alarm.response;

import kh.gangnam.b2b.entity.alarm.Alarm;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
public class ReadAlarm {

    // 알람 읽기 응답 DTO
    // Alarm 엔티티 필드가 존재해야 함
    private Long alarmId;
    private Long userId;
    private String category;
    private String pageUrl;
    private String content;
    private boolean isRead;

    public ReadAlarm(Alarm alarm){
        this.alarmId= alarm.getAlarmId();
        this.userId=alarm.getUserId();
        this.category=alarm.getCategory();
        this.pageUrl=alarm.getPageUrl();
        this.content=alarm.getContent();
        this.isRead=alarm.isRead();
    }

}
