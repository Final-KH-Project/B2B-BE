package kh.gangnam.b2b.WebSocket;

import kh.gangnam.b2b.dto.board.request.BoardSaveResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NotificationPublisher {

    private final RabbitTemplate rabbitTemplate;

    //RabbitMQ exchange,routingKey 설정
    @Value("${spring.rabbitmq.template.exchange}")
    private String exchange;
    @Value("${spring.rabbitmq.template.routing-key}")
    private String routingKey;

    public void publishNoticeEvent(BoardSaveResponse savedPost) {
        rabbitTemplate.convertAndSend(exchange,routingKey,savedPost);
    } //메세지 RabbitMQ로 발행
}
