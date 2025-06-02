package kh.gangnam.b2b.config.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * JWT 인증 과정에서 권한이 없는 요청에 대한 처리를 담당하는 핸들러
 *
 * 이 핸들러는 다음과 같은 상황에서 호출됩니다:
 * 1. 인증은 되었으나 (JWT 토큰이 유효) 요청한 리소스에 대한 권한이 없는 경우
 * 2. 사용자의 역할(ROLE)이 요청한 작업을 수행할 권한이 없는 경우
 *
 * 예시:
 * - 일반 사용자가 관리자 전용 API에 접근하려 할 때
 * - USER 권한을 가진 사용자가 ADMIN 권한이 필요한 작업을 시도할 때
 */
@Component
public class JwtAccessDeniedHandler implements AccessDeniedHandler {

    /**
     * 권한이 없는 요청에 대한 처리를 수행합니다.
     *
     * @param request HTTP 요청 객체
     * @param response HTTP 응답 객체
     * @param accessDeniedException 발생한 접근 거부 예외
     * @throws IOException 응답 작성 중 I/O 오류가 발생할 경우
     *
     * 응답:
     * - HTTP 상태 코드: 403 (FORBIDDEN)
     * - 응답 메시지: "접근이 거부되었습니다."
     */
    @Override
    public void handle(
            HttpServletRequest request,
            HttpServletResponse response,
            AccessDeniedException accessDeniedException
    ) throws IOException {
        response.sendError(HttpServletResponse.SC_FORBIDDEN, "접근이 거부되었습니다.");
    }
}
