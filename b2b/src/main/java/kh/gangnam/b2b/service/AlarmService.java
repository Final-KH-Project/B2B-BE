package kh.gangnam.b2b.service;

import kh.gangnam.b2b.dto.alarm.request.SaveAlarm;
import org.springframework.http.ResponseEntity;

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
     * @param id
     * 알람을 클릭했을 때는 반환값이 알람 페이지로 넘어가는 url 을 주어야 한다.
     * 알람을 단순 읽기 처리만 하는 경우는 url 이 아닌 성공 여부만 반환하면 됨
     * 설계가 끝난 후 토의
     * @return
     */
    ResponseEntity<?> readAlarm(Long id);

    /**
     * 알람 삭제
     * 알람 엔티티의 id 값
     * @param id
     * 알람 삭제 성공 여부 status
     * @return
     */
    ResponseEntity<String> deleteAlarm(Long id);
}
