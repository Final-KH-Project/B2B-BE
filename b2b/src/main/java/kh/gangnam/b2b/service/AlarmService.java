package kh.gangnam.b2b.service;

import kh.gangnam.b2b.dto.alarm.request.SaveAlarm;
import kh.gangnam.b2b.entity.alarm.Alarm;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface AlarmService {


    /**
     * 알람 생성
     * SaveAlarm DTO -> 내부에 알람id, 시간, userId, 분류, 내용 등이 포함되어야 함
     * @param saveAlarm
     * 저장 성공 여부 status
     * @return
     */
    ResponseEntity<?> saveAlarm(SaveAlarm saveAlarm);

    /**
     * 알람 읽기
     * 알람 엔티티의 id 값
     * @param username
     * 알람을 클릭했을 때는 반환값이 알람 페이지로 넘어가는 url 을 주어야 한다.
     * 알람을 단순 읽기 처리만 하는 경우는 url 이 아닌 성공 여부만 반환하면 됨
     * 설계가 끝난 후 토의
     * @return
     */
    //ResponseEntity<?> readAlarm(Long username);

    /**
     * 알람 삭제
     * 알람 엔티티의 id 값
     * @param username
     * 알람 삭제 성공 여부 status
     * @return
     */
    ResponseEntity<String> deleteAlarm(Long username);


    //읽지 않은 알림 갯수 확인용
    Integer unReadCount(Long employeeId);

    Integer countUnReadBoard(String loginId);

    //읽음 처리용
     public void readAlarm(Long alarmId);


    //WebSocketEventListener 에서 호출
    List<Alarm> getUnreadAlarmsByUsername(String username);

    //테스트 코드용
    public void save(Long boardId);

    void markAllAsRead(Long employeeId);
}
