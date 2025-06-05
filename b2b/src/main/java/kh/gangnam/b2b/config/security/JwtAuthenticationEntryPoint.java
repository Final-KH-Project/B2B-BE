package kh.gangnam.b2b.config.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * JWT 인증 실패 시 처리하는 컴포넌트
 * 요청 → SecurityFilterChain → JwtAuthenticationFilter → 인증 실패 → AuthenticationEntryPoint(진입점) → 401 응답
 *
 * 이 클래스는 Spring Security의 AuthenticationEntryPoint를 구현하여,
 * 인증되지 않은 사용자의 요청이나 유효하지 않은 JWT 토큰으로 인한 인증 실패 시
 * 적절한 에러 응답을 생성하고 반환합니다.
 *
 * 주요 기능:
 * - 인증 실패 시 401 Unauthorized 상태 코드 반환
 * - JSON 형식의 에러 메시지 생성
 * - InvalidTokenException 발생 시 구체적인 에러 메시지 제공
 */
@Component
@Slf4j
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    /**
     * 인증 실패 시 호출되는 메서드
     *
     * @param request HTTP 요청 객체
     * @param response HTTP 응답 객체
     * @param authException 인증 실패 시 발생한 예외
     * @throws IOException 응답 작성 중 발생할 수 있는 IO 예외
     *
     * 처리 과정:
     * 1. 기본 에러 메시지 설정 ("인증에 실패했습니다.")
     * 2. InvalidTokenException인 경우 해당 예외의 메시지 사용
     * 3. 응답 헤더 설정 (Content-Type, Status)
     * 4. JSON 형식의 에러 응답 생성 및 전송
     */
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException {

        String errorMessage = "인증에 실패했습니다.";
        log.warn("[AUTH] 인증 실패: {}", errorMessage);
        if (authException instanceof JwtTokenValidationException) {
            errorMessage = authException.getMessage();
        }

        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        Map<String, Object> body = new HashMap<>();
        body.put("status", HttpServletResponse.SC_UNAUTHORIZED);
        body.put("error", "Unauthorized");
        body.put("message", errorMessage);
        body.put("path", request.getRequestURI());

        ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(response.getOutputStream(), body);
    }
}
