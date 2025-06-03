package kh.gangnam.b2b.config.security;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * JWT 토큰의 쿠키 관리를 담당하는 컴포넌트입니다.
 *
 * 주요 기능:
 * 1. 토큰 추출
 *    - HTTP 요청의 쿠키에서 액세스/리프레시 토큰 추출
 *    - 토큰이 없는 경우 null 반환
 *
 * 2. 토큰 저장
 *    - 액세스/리프레시 토큰을 쿠키에 저장
 *    - 보안 설정 적용 (HttpOnly, Secure, Domain, Path)
 *
 * 3. 토큰 삭제
 *    - 로그아웃 시 토큰 쿠키 삭제
 *    - 쿠키 만료 처리
 *
 * 보안 설정:
 * - HttpOnly: JavaScript에서 쿠키 접근 방지
 * - Secure: HTTPS에서만 쿠키 전송
 * - Domain: 지정된 도메인에서만 쿠키 접근 가능
 * - Path: 지정된 경로에서만 쿠키 접근 가능
 *
 * 사용 예시:
 * 1. 로그인 성공 시:
 *    setAccessTokenCookie(response, accessToken);
 *    setRefreshTokenCookie(response, refreshToken);
 *
 * 2. 토큰 검증 시:
 *    String token = getAccessTokenFromCookie(request);
 *
 * 3. 로그아웃 시:
 *    removeAccessTokenCookie(response);
 *    removeRefreshTokenCookie(response);
 *
 * @author board
 * @since 1.0
 */
@Component
public class JwtCookieManager {

    /**
     * 액세스 토큰이 저장된 쿠키의 이름입니다.
     * application.properties의 jwt.cookie.access-name 값을 주입받습니다.
     * 예: "access_token"
     */
    @Value("${jwt.cookie.access-name}")
    private String accessTokenCookieName;

    /**
     * 리프레시 토큰이 저장된 쿠키의 이름입니다.
     * application.properties의 jwt.cookie.refresh-name 값을 주입받습니다.
     * 예: "refresh_token"
     */
    @Value("${jwt.cookie.refresh-name}")
    private String refreshTokenCookieName;

    /**
     * 쿠키가 유효한 도메인입니다.
     * application.properties의 jwt.cookie.domain 값을 주입받습니다.
     * 예: "localhost" 또는 ".example.com"
     */
    @Value("${jwt.cookie.domain}")
    private String cookieDomain;

    /**
     * 쿠키가 유효한 경로입니다.
     * application.properties의 jwt.cookie.path 값을 주입받습니다.
     * 예: "/" 또는 "/api"
     */
    @Value("${jwt.cookie.path}")
    private String cookiePath;

    /**
     * 쿠키의 최대 수명(초)입니다.
     * application.properties의 jwt.cookie.max-age 값을 주입받습니다.
     * 예: 3600 (1시간)
     */
    @Value("${jwt.cookie.max-age}")
    private int cookieMaxAge;

    /**
     * HTTP 요청의 쿠키에서 액세스 토큰을 추출합니다.
     *
     * @param request HTTP 요청 객체
     * @return 추출된 액세스 토큰. 토큰이 없는 경우 null 반환
     * @throws IllegalArgumentException request가 null인 경우
     */
    public String getAccessTokenFromCookie(HttpServletRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("HTTP 요청 객체는 null일 수 없습니다.");
        }
        return getTokenFromCookie(request, accessTokenCookieName);
    }

    /**
     * HTTP 요청의 쿠키에서 리프레시 토큰을 추출합니다.
     *
     * @param request HTTP 요청 객체
     * @return 추출된 리프레시 토큰. 토큰이 없는 경우 null 반환
     * @throws IllegalArgumentException request가 null인 경우
     */
    public String getRefreshTokenFromCookie(HttpServletRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("HTTP 요청 객체는 null일 수 없습니다.");
        }
        return getTokenFromCookie(request, refreshTokenCookieName);
    }

    /**
     * HTTP 요청의 쿠키에서 특정 이름의 토큰을 추출합니다.
     *
     * @param request HTTP 요청 객체
     * @param cookieName 쿠키 이름
     * @return 추출된 토큰. 토큰이 없는 경우 null 반환
     */
    private String getTokenFromCookie(HttpServletRequest request, String cookieName) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookieName.equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

    /**
     * 액세스 토큰을 쿠키에 저장합니다.
     *
     * @param response HTTP 응답 객체
     * @param token 저장할 액세스 토큰
     * @throws IllegalArgumentException response가 null이거나 token이 null/빈 문자열인 경우
     */
    public void setAccessTokenCookie(HttpServletResponse response, String token) {
        validateSetCookieParams(response, token);
        setTokenCookie(response, accessTokenCookieName, token);
    }

    /**
     * 리프레시 토큰을 쿠키에 저장합니다.
     *
     * @param response HTTP 응답 객체
     * @param token 저장할 리프레시 토큰
     * @throws IllegalArgumentException response가 null이거나 token이 null/빈 문자열인 경우
     */
    public void setRefreshTokenCookie(HttpServletResponse response, String token) {
        validateSetCookieParams(response, token);
        setTokenCookie(response, refreshTokenCookieName, token);
    }

    /**
     * 쿠키 저장 파라미터의 유효성을 검증합니다.
     *
     * @param response HTTP 응답 객체
     * @param token 저장할 토큰
     * @throws IllegalArgumentException 유효하지 않은 파라미터인 경우
     */
    private void validateSetCookieParams(HttpServletResponse response, String token) {
        if (response == null) {
            throw new IllegalArgumentException("HTTP 응답 객체는 null일 수 없습니다.");
        }
        if (token == null || token.trim().isEmpty()) {
            throw new IllegalArgumentException("토큰은 null이거나 빈 문자열일 수 없습니다.");
        }
    }

    /**
     * 토큰을 쿠키에 저장합니다.
     *
     * @param response HTTP 응답 객체
     * @param cookieName 쿠키 이름
     * @param token 저장할 토큰
     */
    private void setTokenCookie(HttpServletResponse response, String cookieName, String token) {
        Cookie cookie = new Cookie(cookieName, token);
        cookie.setDomain(cookieDomain);
        cookie.setPath(cookiePath);
        cookie.setMaxAge(cookieMaxAge);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);  // HTTPS에서만 전송
        response.addCookie(cookie);
    }

    /**
     * 액세스 토큰 쿠키를 삭제합니다.
     *
     * @param response HTTP 응답 객체
     * @throws IllegalArgumentException response가 null인 경우
     */
    public void removeAccessTokenCookie(HttpServletResponse response) {
        if (response == null) {
            throw new IllegalArgumentException("HTTP 응답 객체는 null일 수 없습니다.");
        }
        removeTokenCookie(response, accessTokenCookieName);
    }

    /**
     * 리프레시 토큰 쿠키를 삭제합니다.
     *
     * @param response HTTP 응답 객체
     * @throws IllegalArgumentException response가 null인 경우
     */
    public void removeRefreshTokenCookie(HttpServletResponse response) {
        if (response == null) {
            throw new IllegalArgumentException("HTTP 응답 객체는 null일 수 없습니다.");
        }
        removeTokenCookie(response, refreshTokenCookieName);
    }

    /**
     * 특정 이름의 토큰 쿠키를 삭제합니다.
     *
     * @param response HTTP 응답 객체
     * @param cookieName 삭제할 쿠키 이름
     */
    private void removeTokenCookie(HttpServletResponse response, String cookieName) {
        Cookie cookie = new Cookie(cookieName, null);
        cookie.setDomain(cookieDomain);
        cookie.setPath(cookiePath);
        cookie.setMaxAge(0);  // 즉시 만료
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        response.addCookie(cookie);
    }
} 