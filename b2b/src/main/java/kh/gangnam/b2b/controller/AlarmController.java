package kh.gangnam.b2b.controller;


import kh.gangnam.b2b.config.security.CustomEmployeeDetails;
import kh.gangnam.b2b.dto.alarm.AlarmDTO;
import kh.gangnam.b2b.service.AlarmService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;

import java.util.List;


@Slf4j
@Controller
@RequiredArgsConstructor
public class AlarmController {

    private final AlarmService alarmService;


    //읽지 않은 알림 갯수 api
    @GetMapping("/api/employees/{employeeId}/alarm")
    public ResponseEntity<Integer> unReadCount(@PathVariable("employeeId") Long employeeId){
        return ResponseEntity.ok(alarmService.unReadCount(employeeId));
    }
    @GetMapping("/api/employees/{loginId}/alarms/count")
    public ResponseEntity<Integer> unReadCount(@PathVariable("loginId") String loginId){
        return ResponseEntity.ok(alarmService.countUnReadBoard(loginId));
    }

    //알림 목록 호출
    @GetMapping("/api/employees/{loginId}/alarms")
    public ResponseEntity<List<AlarmDTO>> getAlarmList(@PathVariable("loginId") String loginId) {
        List<AlarmDTO> alarms = alarmService.getAlarmsByLoginId(loginId);
        return ResponseEntity.ok(alarms);
    }

    //개별 읽음 처리
    @PutMapping("/api/alarms/{alarmId}/read")
    public ResponseEntity<Void> markAsRead(@PathVariable("alarmId") Long alarmId){
        alarmService.markAsRead(alarmId);
        return ResponseEntity.ok().build();
    }

    //전체 읽음 처리
    @PutMapping("/api/alarms/read")
    public ResponseEntity<Void> markAllAsRead (@AuthenticationPrincipal CustomEmployeeDetails userDetails){
        alarmService.markAllAsRead(userDetails.getEmployeeId());
        //System.out.println(">>>emp_id:"+userDetails.getEmployeeId());
        return ResponseEntity.ok().build();
    }



//    @MessageMapping("/hello") //발행
//    @SendTo ("/topic/message") //단일 메세지 생성
//    public AlarmMessage sendAlarmsViaWebSocket(NotificationMessage message){
//        log.info("웹소켓 알림 수신: {}", message);
//
//        return AlarmMessage.builder().message("메세지 받으세요").build();
//
//    }


}
