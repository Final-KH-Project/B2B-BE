package kh.gangnam.b2b.config;

import jakarta.servlet.http.Cookie;
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
        // ★ 핵심: 리졸버를 리스트의 맨 앞에 추가
        argumentResolvers.add(0, new AuthenticationPrincipalArgumentResolver());
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
        registry.addEndpoint(webSocketEndPoint)
                .setAllowedOriginPatterns("*")
                .addInterceptors(new HandshakeInterceptor() {
                    @Override
                    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response,
                                                   WebSocketHandler wsHandler, Map<String, Object> attributes) {

                        if (request instanceof ServletServerHttpRequest servletRequest) {
                            Cookie cookie = WebUtils.getCookie(
                                    servletRequest.getServletRequest(),
                                    accessTokenCookieName
                            );
                            if (cookie != null) {
                                attributes.put(accessTokenCookieName, cookie.getValue());
                            }
                        }
                        return true;
                    }

                    @Override
                    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception exception) {

                    }
                })
                .withSockJS();
    }
    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // /sub로 시작하는 경로를 구독 브로커로 사용
        registry.enableSimpleBroker("/sub");
        // /pub로 시작하는 경로를 메시지 발행용으로 사용
        registry.setApplicationDestinationPrefixes("/pub");
    }
}
