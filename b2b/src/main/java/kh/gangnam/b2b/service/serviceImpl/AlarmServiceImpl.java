package kh.gangnam.b2b.service.serviceImpl;

import kh.gangnam.b2b.dto.alarm.request.SaveAlarm;
import kh.gangnam.b2b.service.AlarmService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class AlarmServiceImpl implements AlarmService {

    // Alarm 서비스 비즈니스 로직 구현

    @Override
    public ResponseEntity<?> saveAlarm(SaveAlarm saveAlarm) {
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
