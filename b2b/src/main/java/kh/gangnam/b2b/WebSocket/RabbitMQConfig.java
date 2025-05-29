package kh.gangnam.b2b.WebSocket;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * RabbitMQ 설정 클래스
 *
 * 이 클래스는 RabbitMQ와의 통신을 위한 설정을 담당합니다.
 * 주요 기능:
 * 1. RabbitMQ 연결 설정
 * 2. 큐, 익스체인지, 바인딩 정의 (AWS MQ 사용 시 주석 처리)
 * 3. 메시지 리스너 컨테이너 설정
 * 4. 메시지 변환기 설정
 *
 * 메시지 흐름:
 * 프론트엔드 -> WebSocket -> MessageController -> RabbitMQ -> Receiver -> 프론트엔드
 */
@Slf4j
@RequiredArgsConstructor
@Configuration
@EnableRabbit //RabbitMQ 리스너 활성화
public class RabbitMQConfig {

    /**
     * RabbitMQ 연결을 위한 ConnectionFactory
     * Spring Boot의 자동 구성 기능을 통해 주입됨
     * application.properties의 설정을 기반으로 자동 구성
     */
    private final ConnectionFactory connectionFactory;

    /**
     * RabbitMQ 설정값
     * application.properties에서 주입되는 값들
     */
    @Value("${spring.rabbitmq.template.default-receive-queue}")
    private String queue;  // 메시지를 받을 큐 이름

    @Value("${spring.rabbitmq.template.exchange}")
    private String exchange;  // 메시지를 라우팅할 익스체인지 이름

    @Value("${spring.rabbitmq.template.routing-key}")
    private String routingKey;  // 메시지 라우팅 패턴

    /**
     * RabbitMQ 큐 정의
     * AWS MQ에서 수동으로 생성한 경우 @Bean 어노테이션 제거
     *
     * @return Queue 객체
     * @param durable true: 서버 재시작 후에도 큐 유지, false: 임시 큐
     */
    //@Bean  // AWS MQ 사용 시 주석 처리하나, 배포 또는 테스트 시 필요할 수 있음
    Queue queue() {
        return new Queue(queue, true);  // true: 영구 큐
    }

    /**
     * Topic Exchange 정의
     * AWS MQ에서 수동으로 생성한 경우 @Bean 어노테이션 제거
     * Topic Exchange는 라우팅 키 패턴에 따라 메시지를 큐에 전달
     *
     * @return TopicExchange 객체
     */
    //@Bean  // AWS MQ 사용 시 주석 처리
    TopicExchange exchange() {
        return new TopicExchange(exchange);
    }

    /**
     * 큐와 익스체인지 간의 바인딩 정의
     * AWS MQ에서 수동으로 생성한 경우 @Bean 어노테이션 제거
     * 라우팅 키 패턴에 따라 익스체인지에서 큐로 메시지 전달
     *
     * @return Binding 객체
     */
    //@Bean  // AWS MQ 사용 시 주석 처리
    Binding binding() {
        return BindingBuilder.bind(queue()).to(exchange()).with(routingKey);
    }

    /**
     * RabbitMQ 메시지 리스너 컨테이너 정의
     * 큐에서 메시지를 수신하고 처리하는 컨테이너
     *
     * @param receiver 메시지를 처리할 리시버 객체
     * @return SimpleMessageListenerContainer 객체
     */
    @Bean
    SimpleMessageListenerContainer container(Receiver receiver) {
        log.info("RabbitMQ 리스너 컨테이너 초기화 시작");
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();

        // RabbitMQ 연결 설정
        container.setConnectionFactory(connectionFactory);

        // 메시지를 수신할 큐 설정
        container.setQueueNames(queue);

        // 메시지 리스너 설정 (Receiver의 receiveMessage 메서드 사용)
        container.setMessageListener(messageListenerAdapter(receiver));

        log.info("RabbitMQ 리스너 컨테이너 초기화 완료 - 큐: {}", queue);
        return container;
    }

    /**
     * 메시지 리스너 어댑터 정의
     * Receiver의 receiveMessage 메서드를 호출하여 메시지 처리
     *
     * @param receiver 메시지를 처리할 리시버 객체
     * @return MessageListenerAdapter 객체
     */
    @Bean
    MessageListenerAdapter messageListenerAdapter(Receiver receiver) {
        // receiver 객체의 receiveMessage 메서드를 사용하여 메시지 처리
        MessageListenerAdapter messageListenerAdapter = new MessageListenerAdapter(receiver, "receiveMessage");
        // JSON 메시지 변환기 설정
        messageListenerAdapter.setMessageConverter(messageConverter());
        return messageListenerAdapter;
    }

    /**
     * RabbitMQ 메시지 변환기 정의
     * JSON 메시지와 Java 객체 간의 변환을 처리
     *
     * 변환 기준:
     * 1. 기본 생성자 (@NoArgsConstructor) 필수!!!!!!!!!!!
     * 2. Getter/Setter 메서드 (@Getter, @Setter 또는 @Data) 필수!!!!!!!!!!!
     * 3. 추가 설정:
     *    - @JsonProperty: JSON 필드명과 Java 필드명이 다를 때
     *    - @JsonIgnore: 변환에서 제외할 필드
     *    - @JsonFormat: 날짜/시간 형식 지정
     *    - @JsonInclude: null 값 처리 방식
     *
     * @return MessageConverter 객체 (Jackson2JsonMessageConverter)
     */
    @Bean
    MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
