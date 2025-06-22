package kh.gangnam.b2b.config.websocket;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import kh.gangnam.b2b.dto.board.request.BoardSaveResponse;
import kh.gangnam.b2b.dto.board.response.CommentSaveResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Component
public class Receiver { //RabbitMQ 메세지 수신 및 WebSocket 전달 클래스

    //websocket 메세지 프론트 전달용 객체
    private final SimpMessagingTemplate messagingTemplate;

    // 메시지 변환기 주입
    private final MessageConverter messageConverter;


    /**
     * RabbitMQ 메시지 수신 처리
     * @RabbitListener 어노테이션을 사용하여 자동으로 메시지 처리
     *
     * @param message 수신된 메시지
     */

    //@RabbitListener(queues = "${spring.rabbitmq.template.default-receive-queue}")
    @RabbitListener(queues = "#{@alarmQueue.name}")
    public void receiveMessage(Message message) {
        log.info("=== RabbitMQ 메시지 수신 시작 ===");
        log.info("메시지 타입: {}", message.getClass().getName());
        log.info("메시지 헤더: {}", message.getMessageProperties().getHeaders());

        try {
            // MessageProperties에서 __TypeId__ 헤더 확인
            String typeId = (String) message.getMessageProperties().getHeaders().get("__TypeId__");
            log.info("TypeId: {}", typeId);

            // 주입받은 MessageConverter를 사용하여 메시지 변환
            Object convertedMessage = messageConverter.fromMessage(message);

            log.info("변환된 메시지 타입: {}", convertedMessage.getClass().getName());
            log.info("변환된 메시지 내용: {}", convertedMessage);

            // 변환된 객체의 타입에 따라 처리
            if (convertedMessage instanceof BoardSaveResponse event) {
                log.info("BoardSaveResponse 타입 감지됨");
                handlePostCreatedEvent(event);
            } else if (convertedMessage instanceof CommentSaveResponse comment) {
                log.info("CommentSaveResponse 타입 감지됨");
                handleCommentCreatedEvent(comment);

            } else {
                log.warn("지원하지 않는 메시지 타입: {}", convertedMessage.getClass().getName());
                log.warn("메시지 내용: {}", convertedMessage);
            }
        } catch (Exception e) {
            log.error("메시지 처리 중 오류 발생: {}", e.getMessage(), e);
            log.error("문제가 된 메시지: {}", message);
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
     * 댓글 알람 메시지 처리
     * @param comment CommentSaveResponse 객체
     */
    private void handleCommentCreatedEvent(CommentSaveResponse comment) {
        log.info("댓글 알람 메시지 수신: {}", comment);
        messagingTemplate.convertAndSend("/topic/alarms", comment);
    }


}
