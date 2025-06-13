package kh.gangnam.b2b.WebSocket;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.EventListener;

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
 * 5. 개발자별 고유 큐 관리
 * 6. 애플리케이션 종료 시 연결 정리
 *
 * 메시지 흐름:
 * 프론트엔드 -> WebSocket -> MessageController -> RabbitMQ -> Receiver -> 프론트엔드
 */
@Slf4j
//@RequiredArgsConstructor
@Configuration
@EnableRabbit //RabbitMQ 리스너 활성화
public class RabbitMQConfig {

    /**
     * RabbitMQ 연결을 위한 ConnectionFactory
     * Spring Boot의 자동 구성 기능을 통해 주입됨
     * application.properties의 설정을 기반으로 자동 구성
     */
    //private final ConnectionFactory connectionFactory;

    /**
     * RabbitMQ 설정값
     * application.properties에서 주입되는 값들
     */
    @Value("${spring.rabbitmq.template.default-receive-queue}")
    private String queueName;  // 기본 큐 이름

    @Value("${spring.rabbitmq.template.exchange}")
    private String exchange;  // 메시지를 라우팅할 익스체인지 이름

    @Value("${spring.rabbitmq.template.routing-key}")
    private String routingKey;  // 메시지 라우팅 패턴



    /**
     * RabbitMQ 큐 정의
     * 개발자별 고유 큐를 사용하여 메시지 중복 수신 방지
     *
     * @return Queue 객체
     */
    @Bean(name = "alarmQueue")
    @Profile("dev")
    Queue alarmQueueDev() throws Exception {
        String hostname = java.net.InetAddress.getLocalHost().getHostName();
        return new Queue("alarm-queue-" + hostname, false);
    }

    @Bean(name = "alarmQueue")
    @Profile("prod")
    Queue alarmQueueProd() {
        return new Queue(queueName, true); // durable: true
    }

    /**
     * Topic Exchange 정의
     * AWS MQ에서 수동으로 생성한 경우 @Bean 어노테이션 제거
     * Topic Exchange는 라우팅 키 패턴에 따라 메시지를 큐에 전달
     *
     * @return TopicExchange 객체
     */
    @Bean  // AWS MQ 사용 시 주석 처리
    TopicExchange exchange() {
        log.info("RabbitMQ Exchange 생성: {}", exchange);
        return new TopicExchange(exchange);
    }

    /**
     * 큐와 익스체인지 간의 바인딩 정의
     * AWS MQ에서 수동으로 생성한 경우 @Bean 어노테이션 제거
     * 라우팅 키 패턴에 따라 익스체인지에서 큐로 메시지 전달
     *
     * @return Binding 객체
     * @throws Exception
     */
    // 운영 환경: 고정 큐 + 바인딩

    @Bean
    @Profile("prod")
    Binding bindingProd() {
        return BindingBuilder.bind(alarmQueueProd()).to(exchange()).with(routingKey);
    }
    @Bean
    @Profile("dev")
    Binding bindingDev() throws Exception {
        return BindingBuilder.bind(alarmQueueDev()).to(exchange()).with(routingKey);
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

    /**
     * 애플리케이션 종료 시 RabbitMQ 연결 정리
     */
    @EventListener
    public void handleContextClosed(ContextClosedEvent event) {
        log.info("애플리케이션 종료 - RabbitMQ 연결 정리 시작");
        try {
            // 애플리케이션 종료 시 로그만 남기고 실제 정리는 Spring Boot가 자동으로 처리
            log.info("RabbitMQ 연결 정리 완료 - Spring Boot가 자동으로 처리");
        } catch (Exception e) {
            log.error("RabbitMQ 연결 정리 중 오류 발생: {}", e.getMessage(), e);
        }
    }
}
