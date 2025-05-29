package kh.gangnam.b2b.WebSocket;

import kh.gangnam.b2b.dto.board.request.BoardSaveResponse;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Component
public class Receiver { //RabbitMQ 메세지 수신 및 WebSocket 전달 클래스

    //websocket 메세지 프론트 전달용 객체
    private final SimpMessagingTemplate messagingTemplate;

    //메세지 전달
    public void receiveMessage(Object message) {
        try {
            // Java 16부터 도입된 instanceof 패턴 매칭: 타입 체크와 캐스팅을 동시에 수행
            if (message instanceof BoardSaveResponse event) {
                handlePostCreatedEvent(event);
            } else if (message instanceof AlarmMessage alarmMessage) {
                handleGreeting(alarmMessage);
            } else {
                log.warn("지원하지 않는 메시지 타입: {}", message.getClass().getName());
            }
        } catch (Exception e) {
            log.error("메시지 처리 중 오류 발생: {}", e.getMessage(), e);
        }
    }
    /**
     * 공지사항 알람 메시지 처리
     * @param event BoardSaveResponse 객체
     */
    private void handlePostCreatedEvent(BoardSaveResponse event) {
        log.info("알람 메시지 수신: {}", event);
        messagingTemplate.convertAndSend("/topic/alarms", event);
    }

    /**
     * 일반 메시지 처리
     * @param alarmMessage Greeting 객체
     */
    private void handleGreeting(AlarmMessage alarmMessage) {
        log.info("일반 메시지 수신: {}", alarmMessage);
        messagingTemplate.convertAndSend("/topic/greetings", alarmMessage);
    }

}
