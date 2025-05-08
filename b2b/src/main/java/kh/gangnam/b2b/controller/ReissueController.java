package kh.gangnam.b2b.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kh.gangnam.b2b.dto.auth.LoginResponse;
import kh.gangnam.b2b.entity.RefreshEntity;
import kh.gangnam.b2b.repository.RefreshRepository;
import kh.gangnam.b2b.security.JWTUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
public class ReissueController {

    private final JWTUtil jwtUtil;
    private final ObjectMapper objectMapper;
    private final RefreshRepository refreshRepository;


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

        String username = jwtUtil.getUsername(refresh);
        String role = jwtUtil.getRole(refresh);

        //make new JWT, new Refresh 3,600,000ms = 1시간
        String newAccess = jwtUtil.createJwt("access", username, role, 3600L);
        // 86,400,000 = 하루
        String newRefresh = jwtUtil.createJwt("refresh", username, role, 20000L);
        LocalDateTime expiresAt = LocalDateTime.now().plusSeconds(3600L);

        // RefreshEntity 저장
        addRefreshEntity(username, newRefresh, expiresAt);

        // LoginResponse 객체 생성
        LoginResponse loginResponse = LoginResponse.builder()
                .accessToken(newAccess)
                .expiresAt(expiresAt)
                .build();

        //response
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
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
        cookie.setMaxAge(24*60*60);
        //cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setHttpOnly(true);

        return cookie;
    }

    private void addRefreshEntity(String username, String refresh, LocalDateTime expiredMs) {

        // 기존 토큰 삭제
        refreshRepository.findByUsername(username)
                .ifPresent(refreshRepository::delete);

        // 새 토큰 저장
        RefreshEntity refreshEntity = RefreshEntity.builder()
                .username(username)
                .refresh(refresh)
                .expiresAt(expiredMs)
                .build();

        refreshRepository.save(refreshEntity);
    }
}