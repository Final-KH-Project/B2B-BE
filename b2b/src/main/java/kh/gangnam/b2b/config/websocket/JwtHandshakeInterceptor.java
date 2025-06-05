package kh.gangnam.b2b.config.websocket;

import kh.gangnam.b2b.config.security.JwtCookieManager;
import kh.gangnam.b2b.config.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import jakarta.servlet.http.HttpServletRequest;

import java.util.*;

@Slf4j
@RequiredArgsConstructor
@Component // 스프링 빈으로 등록
public class JwtHandshakeInterceptor implements HandshakeInterceptor {

    private final JwtCookieManager jwtCookieManager;
    private final JwtTokenProvider jwtTokenProvider;

        @Override
        public boolean beforeHandshake(
                ServerHttpRequest request,
                ServerHttpResponse response,
                WebSocketHandler wsHandler,
                Map<String, Object> attributes
        ) {
            // 1. HttpServletRequest 로 형변환
            if (!(request instanceof ServletServerHttpRequest)) {
                return false;
            }
            HttpServletRequest servletRequest = ((ServletServerHttpRequest) request).getServletRequest();

            log.info("[HandshakeInterceptor] WebSocket 요청 {}",servletRequest.getRequestURI());
            log.info("Query String: " + servletRequest.getQueryString());

            // 2. 쿠키에서 access 꺼내기
            String access = jwtCookieManager.getAccessTokenFromCookie(servletRequest);

            // 3. 토큰 유효성 검사
            try {
                if (!jwtTokenProvider.validateToken(access)) {
                    response.setStatusCode(HttpStatus.UNAUTHORIZED);
                    return false;
                }

                attributes.put("access", access);
                return true;
            } catch (Exception ex) {
                log.error("WebSocket 연결 거부: {}", ex.getMessage());
                response.setStatusCode(HttpStatus.FORBIDDEN);
                return false;
            }
        }

        @Override
        public void afterHandshake(
                ServerHttpRequest request,
                ServerHttpResponse response,
                WebSocketHandler wsHandler,
                Exception exception
        ) {
            // 핸드셰이크 후 로직 필요 시 작성
        }

    } //HandShaker Interceptor: 세션 정보를 가져 옴, 로그인 된 사용자만 웹소켓을 이용가능 하게 함
