package kh.gangnam.b2b.security;

import org.springframework.security.core.AuthenticationException;

/**
 * JWT 토큰 인증 과정에서 발생하는 유효하지 않은 토큰 예외를 처리하는 클래스입니다.
 * 현재 JwtAuthenticationFilter에서 발생하는 예외를 처리하고 있습니다.
 *
 * 이 예외는 다음과 같은 상황에서 발생할 수 있습니다:
 * - 만료된 토큰
 * - 잘못된 서명의 토큰
 * - 지원하지 않는 토큰 형식
 * - 토큰이 null이거나 빈 문자열인 경우
 *
 * @author board
 * @version 1.0
 * @since 2024
 */
public class JwtTokenValidationException extends AuthenticationException {
    /**
     * 직렬화 버전 UID
     */
    private static final long serialVersionUID = 1L;

    /**
     * 메시지만을 포함하는 JwtTokenValidationException 생성합니다.
     *
     * @param message 예외 메시지
     */
    public JwtTokenValidationException(String message) {
        super(message);
    }

    /**
     * 메시지와 원인(cause)을 포함하는 JwtTokenValidationException 생성합니다.
     *
     * @param message 예외 메시지
     * @param cause 원인이 되는 예외
     */
    public JwtTokenValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}
