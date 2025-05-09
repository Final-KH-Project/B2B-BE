package kh.gangnam.b2b.dto.alarm;

import kh.gangnam.b2b.entity.alarm.Alarm;

//to Dto - mapping
public class AlarmDTO {
    private Long alarmId;
    private String category;
    private String pageUrl;
    private String content;
    private boolean isRead;

    public static AlarmDTO alarmDTO(Alarm alarm){ //변환용 메서드
        AlarmDTO dto=new AlarmDTO();
        dto.alarmId= alarm.getAlarmId();
        dto.category= alarm.getCategory();
        dto.pageUrl= alarm.getPageUrl();
        dto.content= alarm.getContent();
        dto.isRead=alarm.isRead();
        return dto;
    }


} //userId 제외: 해당 유저가 조회하는 상황이라 제외함
