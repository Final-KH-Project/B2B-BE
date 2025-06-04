package kh.gangnam.b2b.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kh.gangnam.b2b.dto.auth.response.LoginResponse;
import kh.gangnam.b2b.entity.auth.Refresh;
import kh.gangnam.b2b.repository.RefreshRepository;
import kh.gangnam.b2b.security.JWTUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.time.LocalDateTime;

@RestController
@RequiredArgsConstructor
public class ReissueController {

    private final JWTUtil jwtUtil;
    private final ObjectMapper objectMapper;
    private final RefreshRepository refreshRepository;

    @Value("${jwt.token.accessExpiration}")
    private Long accessExpired;

    @Value("${jwt.token.refreshExpiration}")
    private Long refreshExpired;


    @PostMapping("/api/auth/reissue")
    public ResponseEntity<?> reissue(HttpServletRequest request, HttpServletResponse response) {

        //get refresh token
        String refresh = null;
        Cookie[] cookies = request.getCookies();
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals("refresh")) {
                refresh = cookie.getValue();
            }
        }
        // refresh 가 쿠키에 없는 경우
        if (refresh == null) {

            //response status code
            return new ResponseEntity<>("refresh token null", HttpStatus.BAD_REQUEST);
        }
        // DB에 refresh 토큰 존재 확인
        if (!refreshRepository.existsByRefresh(refresh)) {
            Cookie cookie = new Cookie("refresh", null);
            cookie.setMaxAge(0); // 즉시 만료
            response.addCookie(cookie);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "refresh token not found in DB");
        }

        //expired check
        try {
            jwtUtil.isExpired(refresh);
        } catch (ExpiredJwtException e) {

            //response status code
            return new ResponseEntity<>("refresh token expired", HttpStatus.BAD_REQUEST);
        }

        // 토큰이 refresh인지 확인 (발급시 페이로드에 명시)
        String category = jwtUtil.getCategory(refresh);

        if (!category.equals("refresh")) {

            //response status code
            return new ResponseEntity<>("invalid refresh token", HttpStatus.BAD_REQUEST);
        }

        String loginId = jwtUtil.getLoginId(refresh);
        String role = jwtUtil.getRole(refresh);
        Long employeeId = jwtUtil.getEmployeeId(refresh);

        //make new JWT, new Refresh 3,600,000ms = 1시간
        String newAccess = jwtUtil.createJwt("access", loginId, employeeId, role, accessExpired);
        // 86,400,000 = 하루
        String newRefresh = jwtUtil.createJwt("refresh", loginId, employeeId, role, refreshExpired);
        LocalDateTime expiresAt = LocalDateTime.now().plusSeconds(accessExpired/1000);

        // RefreshEntity 저장
        addRefreshEntity(employeeId, newRefresh, expiresAt);

        // LoginResponse 객체 생성
        LoginResponse loginResponse = LoginResponse.builder()
                .expiresAt(expiresAt)
                .build();

        //response
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        // set cookie
        response.addCookie(createCookie("access", newAccess));
        response.addCookie(createCookie("refresh", newRefresh));
        try {
            objectMapper.writeValue(response.getWriter(), loginResponse);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return new ResponseEntity<>(HttpStatus.OK);
    }
    // cookie 생성 로직
    private Cookie createCookie(String key, String value) {

        Cookie cookie = new Cookie(key, value);
        cookie.setMaxAge(key.equals("access") ? (int) (accessExpired / 1000) : (int) (refreshExpired / 1000));
        //cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setHttpOnly(true);

        return cookie;
    }

    private void addRefreshEntity(Long employeeId, String refresh, LocalDateTime expiredMs) {

        refreshRepository.deleteByEmployeeId(employeeId);
        // 새 토큰 저장
        Refresh refreshEntity = Refresh.builder()
                .employeeId(employeeId)
                .refresh(refresh)
                .expiresAt(expiredMs)
                .build();

        refreshRepository.save(refreshEntity);
    }
}