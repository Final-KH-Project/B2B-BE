package kh.gangnam.b2b.controller;


import kh.gangnam.b2b.webSocket.AlarmMessage;
import kh.gangnam.b2b.webSocket.NotificationMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;


@Slf4j
@Controller
//@RequiredArgsConstructor
public class AlarmController {
    // alarm 엔트포인트 Controller

   // private final AlarmServiceImpl alarmServiceImpl;

    @MessageMapping("/hello") //발행
    @SendTo ("/topic/message") //메세지 생성
    public AlarmMessage sendAlarmViaWebSocket(NotificationMessage message){
        log.info("웹소켓 알림 수신: {}", message);

        return AlarmMessage.builder().message("메세지 받으세요").build();

    }

}
