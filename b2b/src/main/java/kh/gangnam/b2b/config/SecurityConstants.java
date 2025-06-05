package kh.gangnam.b2b.config;

/**
 * 보안 설정에 사용되는 상수들을 정의하는 클래스입니다.
 *
 * 이 클래스는 Spring Security 설정에서 인증이 필요하지 않은 공개 접근 가능한 URL들을 정의합니다.
 * 주로 SecurityConfig 클래스에서 사용되며, 다음과 같은 목적으로 활용됩니다:
 *
 * 1. 인증이 필요하지 않은 공개 API 엔드포인트 정의
 * 2. HTTP 메소드별(GET, POST)로 접근 가능한 URL 구분
 * 3. WebSocket 엔드포인트에 대한 접근 제어
 *
 * 사용 예시:
 * - SecurityConfig에서 permitAll() 설정에 사용
 * - WebSecurityCustomizer에서 ignoring() 설정에 사용
 * - SecurityFilterChain에서 requestMatchers() 설정에 사용
 *
 * @see
 */
public final class SecurityConstants{

    private SecurityConstants() {} // 인스턴스화 방지

    /**
     * GET 메소드로 접근 가능한 공개 URL 목록입니다.
     * 이 URL들은 인증 없이 접근이 가능하며, 다음과 같은 엔드포인트들을 포함합니다:
     * - 게시글 조회 관련 API (/api/boards/**)
     * - 댓글 조회 관련 API (/api/comments/**)
     * - WebSocket 연결 관련 엔드포인트 (/ws/**)
     *
     * SecurityConfig에서 이 URL들은 permitAll() 설정에 사용됩니다.
     */
    public static final String[] PUBLIC_GET_URLS = {
            // 게시글
            "/api/boards"               // GET: 게시글 목록
            ,"/api/boards/{id}"          // GET: 게시글 상세
            ,"/api/public/**"            // 공개 게시글

            // 댓글
            ,"/api/comments"             // GET: 댓글 목록
            ,"/api/comments/{id}"        // GET: 특정 댓글
            ,"/api/boards/{boardId}/comments"  // GET: 특정 게시글의 댓글 목록

            //승인 권한
            ,"/api/approval"

            // WebSocket
            ,"/ws"                     // WebSocket 기본 엔드포인트
            ,"/ws/**"                  // WebSocket 하위 엔드포인트
    };

    /**
     * POST 메소드로 접근 가능한 공개 URL 목록입니다.
     * 이 URL들은 인증 없이 접근이 가능하며, 다음과 같은 엔드포인트들을 포함합니다:
     * - 회원가입 (/api/users)
     * - 로그인 (/api/auth/login)
     * - 토큰 갱신 (/api/auth/refresh)
     *
     * SecurityConfig에서 이 URL들은 permitAll() 설정에 사용됩니다.
     */
    public static final String[] PUBLIC_POST_URLS = {
            "/api/auth/join"                 // POST: 회원가입
            // 인증 관련
            ,"/api/auth/login"           // 로그인
            ,"/api/auth/reissue"         // 토큰 갱신
    };

}
