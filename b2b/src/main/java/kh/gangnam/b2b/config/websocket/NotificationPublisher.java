package kh.gangnam.b2b.config.websocket;

import kh.gangnam.b2b.dto.board.request.BoardSaveResponse;
import kh.gangnam.b2b.dto.board.response.CommentSaveResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NotificationPublisher {

    private final RabbitTemplate rabbitTemplate;
    //private final SimpMessagingTemplate messagingTemplate;

    //RabbitMQ exchange,routingKey 설정
    @Value("${spring.rabbitmq.template.exchange}")
    private String exchange;
    @Value("${spring.rabbitmq.template.routing-key}")
    private String routingKey;

    public void publishNoticeEvent(BoardSaveResponse savedPost) {
        rabbitTemplate.convertAndSend(exchange,routingKey,savedPost);

        //messagingTemplate.convertAndSend("/topic/alarms", savedPost);
    } //메세지 RabbitMQ로 발행

    public void publishCommentEvent(CommentSaveResponse savedComment) {
        rabbitTemplate.convertAndSend(exchange,routingKey,savedComment);
        //messagingTemplate.convertAndSend("/topic/alarms", savedComment);
    } //메세지 RabbitMQ로 발행
}
