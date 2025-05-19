package kh.gangnam.b2b.service.ServiceImpl;

import kh.gangnam.b2b.dto.alarm.request.SaveAlarm;
import kh.gangnam.b2b.service.AlarmService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class AlarmServiceImpl implements AlarmService {

    // Alarm 서비스 비즈니스 로직 구현

    @Override
    public ResponseEntity<?> saveAlarm(SaveAlarm saveAlarm) {

        // TODO 알람 전송 시 알람 데이터베이스 저장

        // TODO 알람을 수신한 사람은 알람에 접근할 수 있어야 함
        return null;
    }

    @Override
    public ResponseEntity<?> readAlarm(Long id) {
        return null;
    }

    @Override
    public ResponseEntity<String> deleteAlarm(Long id) {
        return null;
    }


}
