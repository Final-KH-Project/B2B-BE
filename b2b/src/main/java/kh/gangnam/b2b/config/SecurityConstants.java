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
            // 회원가입 중복체크
            "/api/auth/check-loginId"

            // WebSocket
            ,"/ws"                     // WebSocket 기본 엔드포인트
            ,"/ws/**"                  // WebSocket 하위 엔드포인트
            ,"/ws-stomp"
            ,"/ws-stomp/**"
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

    /**
     * 인사 권한 이상만 있는 사람들이 사용할 수 있는 URL 입니다.
     * SecurityConfig에서 이 URL들은 인사 권한 이상 ROLE 설정을 해야합니다.
     */
    public static final String[] HR_URLS = {
//            "/api/approval/pending",                // 부서장 승인 대기 목록 조회
//            "/api/approval/{requestId}/decision",   // 부서장 승인/반려 처리
            "/api/dept/update/head",                // 부서장 지정
            "/api/dept/create",                     // 부서 생성
            "/api/dept/update/mentor",              // 부서 내 사수 지정
            "/api/dept/move/employee",              // 사원 부서 변경
            "/api/employee/position",               // 사원 직급 변경
            "api/hr/salary",                        // 단일 급여 생성|수정
            "/api/hr/salary/{salaryId}/pay",        // 단일 급여 지급
            "/api/hr/salary/pay/all/targetMonth/{targetMonth}", // 전체 사원 급여 예정 지급
            "/api/hr/salary/pay/dept/{deptId}/targetMonth/{targetMonth}", // 부서별 사원 급여 일괄 지급
            "/api/hr/salary/date/{date}",           // 전체 사원 급여 조회
            "/api/hr/salary/dept/{deptId}/date/{date}",     // 부서별 급여 조회
            "/api/hr/salary/auto-generate/date/{date}",      // 누락 급여 자동 생성
            "/api/meeting/create/room",             // 회의실 생성
    };

    /**
     * 부서장이 사용할 수 있는 URL 입니다.
     * SecurityConfig에서 이 URL들은 부서장 ROLE 설정을 해야합니다.
     */
    public static final String[] DEPT_HEAD_URLS = {
            "/api/approval/pending",                // 부서장 승인 대기 목록 조회
            "/api/approval/{requestId}/decision",   // 부서장 승인/반려 처리
    };

}
