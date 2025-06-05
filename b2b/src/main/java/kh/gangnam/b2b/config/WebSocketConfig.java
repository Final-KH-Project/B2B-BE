package kh.gangnam.b2b.config;

import jakarta.servlet.http.Cookie;
import kh.gangnam.b2b.config.websocket.CustomHandshakeHandler;
import kh.gangnam.b2b.config.websocket.JwtHandshakeInterceptor;
import kh.gangnam.b2b.security.StompJwtChannelInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.messaging.handler.invocation.HandlerMethodArgumentResolver;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.security.messaging.context.AuthenticationPrincipalArgumentResolver;
import org.springframework.web.reactive.socket.server.support.WebSocketHandlerAdapter;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.server.HandshakeInterceptor;
import org.springframework.web.util.WebUtils;

import java.util.List;
import java.util.Map;

/**
 * WebSocket & STOMP 메시지 브로커 설정
 */
@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final StompJwtChannelInterceptor stompJwtChannelInterceptor;
    private final JwtHandshakeInterceptor jwtHandshakeInterceptor;
    private final CustomHandshakeHandler customHandshakeHandler;

    @Value("${cors.allowed-origins}")
    private String[] allowedOrigins;

    @Value("${jwt.cookie.access-name}")
    private String accessTokenCookieName;

    @Value("${websocket.end-point}")
    private String webSocketEndPoint;

    @Bean
    public WebSocketHandlerAdapter handlerAdapter() {

        return new WebSocketHandlerAdapter();
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
        argumentResolvers.add(new AuthenticationPrincipalArgumentResolver());
    }

    // 추가: 클라이언트 인바운드 채널 설정
    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(
                stompJwtChannelInterceptor
        );
    }
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // webSocket 엔드포인트 설정(클라이언트 연결 주소)
        registry.addEndpoint(webSocketEndPoint)
                .setAllowedOriginPatterns("*")
                .addInterceptors(jwtHandshakeInterceptor)
                .setHandshakeHandler(customHandshakeHandler)
                .withSockJS();
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("*")
                .addInterceptors(jwtHandshakeInterceptor)
                .setHandshakeHandler(customHandshakeHandler)
                .withSockJS();
    }
    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // sub 로 시작하는 경로를 구독 브로커로 사용
        // 메세지 생성 엔드포인트
        registry.enableSimpleBroker("/sub", "/topic", "/queue");
        // /pub 로 시작하는 경로를 메시지 발행용으로 사용
        registry.setApplicationDestinationPrefixes("/pub");
        registry.setUserDestinationPrefix("/user");
    }
}
