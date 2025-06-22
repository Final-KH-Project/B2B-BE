package kh.gangnam.b2b.config.websocket;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * RabbitMQ 연결 상태 모니터링 및 관리 클래스
 * 개발 환경에서 연결 상태를 추적하고 문제를 조기에 발견
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class RabbitMQConnectionManager {

    private final ConnectionFactory connectionFactory;
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    @PostConstruct
    public void init() {
        log.info("RabbitMQ 연결 관리자 초기화");

        // 연결 상태 모니터링 시작 (30초마다 체크)
        scheduler.scheduleAtFixedRate(this::checkConnectionStatus, 30, 30, TimeUnit.SECONDS);

        // 초기 연결 상태 로깅
        logConnectionStatus();
    }

    @PreDestroy
    public void cleanup() {
        log.info("RabbitMQ 연결 관리자 정리 시작");

        try {
            // 스케줄러 종료
            scheduler.shutdown();
            if (!scheduler.awaitTermination(5, TimeUnit.SECONDS)) {
                scheduler.shutdownNow();
            }

            // @RabbitListener는 Spring이 자동으로 정리하므로 별도 정리 불필요
            log.info("RabbitMQ 연결 관리자 정리 완료");
        } catch (Exception e) {
            log.error("RabbitMQ 연결 관리자 정리 중 오류 발생: {}", e.getMessage(), e);
        }
    }

    /**
     * 연결 상태 확인
     */
    private void checkConnectionStatus() {
        try {
            boolean isConnected = connectionFactory.createConnection().isOpen();

            log.debug("RabbitMQ 연결 상태 - 연결: {}", isConnected);

            if (!isConnected) {
                log.warn("RabbitMQ 연결이 끊어졌습니다!");
            }

        } catch (Exception e) {
            log.error("RabbitMQ 연결 상태 확인 중 오류 발생: {}", e.getMessage());
        }
    }

    /**
     * 현재 연결 상태 로깅
     */
    public void logConnectionStatus() {
        try {
            boolean isConnected = connectionFactory.createConnection().isOpen();

            log.info("=== RabbitMQ 연결 상태 ===");
            log.info("연결 상태: {}", isConnected ? "연결됨" : "연결 안됨");
            log.info("리스너 상태: Spring @RabbitListener가 자동 관리됨");
            log.info("========================");

        } catch (Exception e) {
            log.error("연결 상태 로깅 중 오류 발생: {}", e.getMessage());
        }
    }

    /**
     * 연결 상태 반환
     */
    public boolean isConnected() {
        try {
            return connectionFactory.createConnection().isOpen();
        } catch (Exception e) {
            log.error("연결 상태 확인 중 오류: {}", e.getMessage());
            return false;
        }
    }

}
