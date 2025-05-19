package kh.gangnam.b2b.webSocket;


import lombok.RequiredArgsConstructor;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@RequiredArgsConstructor
@EnableWebSocketMessageBroker //stomp webSocket 서버 활성화
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final JwtHandshakeInterceptor jwtHandshakeInterceptor; //JwtHandshakeInterceptor 주입

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws") //webSocket 엔드포인트 설정 (클라이언트 연결 주소)
                .addInterceptors(jwtHandshakeInterceptor) //jwt 인증 인터셉터 연결
                .setAllowedOriginPatterns("http://localhost:3000") //cors 허용
                .withSockJS(); //SockJS(프론트 라이브러리) fallback 지원. 웹소켓 미지원 브라우저 대비용

    }

    //메세지 브로커 설정
    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        //메세지 생성 엔드포인트
        registry.enableSimpleBroker("/topic","/queue");
        //stomp 메세지 발행 엔드포인트
        registry.setApplicationDestinationPrefixes("/pub");

    }

}

