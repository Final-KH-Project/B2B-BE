package kh.gangnam.b2b.config;

import jakarta.annotation.PostConstruct;
import kh.gangnam.b2b.config.security.JwtAccessDeniedHandler;
import kh.gangnam.b2b.config.security.JwtAuthenticationEntryPoint;
import kh.gangnam.b2b.config.security.JwtAuthenticationFilter;
import kh.gangnam.b2b.config.security.CustomEmployeeDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    @Value("${cors.allowed-origins}")
    private String[] allowedOrigins;

    @Value("${websocket.allowed}")
    private String webSocketEndPoint;

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final CustomEmployeeDetailsService customEmployeeDetailsService;


    @PostConstruct
    public void init() {
        SecurityContextHolder.setStrategyName(
                SecurityContextHolder.MODE_INHERITABLETHREADLOCAL
        );
    }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // CSRF 보호 비활성화
                .csrf(AbstractHttpConfigurer::disable)
                // Form 로그인 방식 비활성화
                .formLogin(AbstractHttpConfigurer::disable)
                // http basic 인증 방식 비활성화
                .httpBasic(AbstractHttpConfigurer::disable)
                // CORS 설정
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                // 세션 관리 설정
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // 인증 예외 처리
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                        .accessDeniedHandler(jwtAccessDeniedHandler))
                // 요청 인증 설정
                // API와 WebSocket 요청에 JWT 필터 적용
                .securityMatcher("/api/**", webSocketEndPoint, "/ws/**", "/ws", "/ws-stomp")
                .authorizeHttpRequests(auth -> auth
                        // GET 요청 중 공개 접근 가능한 URL
                        .requestMatchers(HttpMethod.GET, SecurityConstants.PUBLIC_GET_URLS).permitAll()
                        // POST 요청 중 공개 접근 가능한 URL
                        .requestMatchers(HttpMethod.POST, SecurityConstants.PUBLIC_POST_URLS).permitAll()
                        .requestMatchers("/error").permitAll()
                        // 부서장만 접근 가능한 URL
                        .requestMatchers(SecurityConstants.DEPT_HEAD_URLS).hasAnyRole("ADMIN", "HEAD")
                        // 인사 직급 이상만 접근 가능한 URL
                        .requestMatchers(SecurityConstants.HR_URLS).hasRole("ADMIN")

                        .requestMatchers("/ws-stomp",webSocketEndPoint).authenticated()
                        .requestMatchers("/ws", "/ws/**").authenticated()

                        // 나머지 요청 인증 필요
                        .anyRequest().authenticated())
                // UserDetailsService 설정
                .userDetailsService(customEmployeeDetailsService)
                // JWT 필터 추가
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
    /**
     * CORS(Cross-Origin Resource Sharing) 설정을 정의합니다.
     *
     * 주요 설정:
     * - 허용된 출처(Origin): 프론트엔드 도메인
     * - 허용된 HTTP 메서드: GET, POST, PUT, DELETE, OPTIONS
     * - 허용된 헤더: 모든 헤더
     * - 인증 정보 포함: true (쿠키, 인증 헤더 등 전송 가능)
     *
     * @return CORS 설정이 적용된 ConfigurationSource
     */
    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // 허용할 출처(Origin) 설정
        configuration.setAllowedOrigins(Arrays.asList(allowedOrigins));

        // 허용할 HTTP 메서드 설정
        configuration.setAllowedMethods(Arrays.asList(
                HttpMethod.GET.name(),
                HttpMethod.POST.name(),
                HttpMethod.PUT.name(),
                HttpMethod.DELETE.name(),
                HttpMethod.OPTIONS.name()
        ));

        // 허용할 헤더 설정
        configuration.setAllowedHeaders(Arrays.asList(
                HttpHeaders.AUTHORIZATION,
                HttpHeaders.CONTENT_TYPE,
                HttpHeaders.ACCEPT,
                HttpHeaders.ORIGIN,
                HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD,
                HttpHeaders.ACCESS_CONTROL_REQUEST_HEADERS,
                "Sec-WebSocket-Key",           // WebSocket 핸드셰이크
                "Sec-WebSocket-Version",       // WebSocket 버전
                "Sec-WebSocket-Protocol",      // WebSocket 프로토콜
                "Sec-WebSocket-Extensions",     // WebSocket 확장
                "cookie"
        ));

        // 인증 정보 포함
        configuration.setAllowCredentials(true);

        // 노출할 헤더 설정
        configuration.setExposedHeaders(Arrays.asList(
                "Sec-WebSocket-Accept",
                "Sec-WebSocket-Protocol",
                "Sec-WebSocket-Extensions",
                "cookie"
        ));
        // CORS 설정을 적용할 URL 패턴 설정
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }

    // 비밀번호 인코더
    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // 인증 매니저
    @Bean
    AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}