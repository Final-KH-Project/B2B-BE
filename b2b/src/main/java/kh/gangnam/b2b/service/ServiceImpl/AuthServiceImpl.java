package kh.gangnam.b2b.service.ServiceImpl;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kh.gangnam.b2b.config.security.CustomEmployeeDetails;
import kh.gangnam.b2b.dto.auth.request.JoinRequest;
import kh.gangnam.b2b.dto.auth.request.LoginRequest;
import kh.gangnam.b2b.dto.auth.response.LoginResponse;
import kh.gangnam.b2b.entity.auth.Employee;
import kh.gangnam.b2b.repository.EmployeeRepository;
import kh.gangnam.b2b.config.security.JwtCookieManager;
import kh.gangnam.b2b.config.security.JwtTokenProvider;
import kh.gangnam.b2b.config.security.RefreshTokenService;
import kh.gangnam.b2b.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final JwtCookieManager jwtCookieManager;
    private final RefreshTokenService refreshTokenService;
    private final EmployeeRepository employeeRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    /**
     * 회원 가입 로직
     * @param joinRequest
     * @return
     * 이미 존재하는 이름일 경우 (409)
     * 가입 성공 (200)
     */
    public ResponseEntity<String> join(JoinRequest joinRequest) {

        String loginId = joinRequest.getLoginId();
        String password = joinRequest.getPassword();

        Boolean isExist = employeeRepository.existsByLoginId(loginId);

        if (isExist) {

            return ResponseEntity.status(HttpStatus.CONFLICT).body(loginId + "는 이미 존재하는 아이디입니다.");
        }

        // 비밀번호 인코딩
        String encodedPassword = bCryptPasswordEncoder.encode(password);

        // Employee 엔티티로 변환
        Employee employee = joinRequest.toEntity(encodedPassword, "ROLE_ADMIN");

        employeeRepository.save(employee);

        return ResponseEntity.ok(loginId + " 회원가입 완료");
    }
    public ResponseEntity<?> login(LoginRequest loginRequest, HttpServletResponse response) {
        // 인증 처리
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getLoginId(), loginRequest.getPassword())
        );
        // 토큰 생성
        String accessToken = jwtTokenProvider.generateAccessToken(authentication);
        String refreshToken = jwtTokenProvider.generateRefreshToken(authentication);

        // Refresh 토큰 삭제 및 DB 저장
        CustomEmployeeDetails userDetails = (CustomEmployeeDetails) authentication.getPrincipal();
        refreshTokenService.deleteByEmployeeId(userDetails.getEmployeeId());
        refreshTokenService.save(userDetails.getEmployeeId(), refreshToken);

        // 쿠키에 토큰 저장
        jwtCookieManager.setAccessTokenCookie(response, accessToken);
        jwtCookieManager.setRefreshTokenCookie(response, refreshToken);

        // 응답 생성
        Date expiresAt = jwtTokenProvider.extractExpiration(accessToken);
        String loginId = userDetails.getUsername();
        String name = userDetails.getRealName();
        return ResponseEntity.ok(new LoginResponse(loginId, expiresAt, name));
    }
    public ResponseEntity<?> logout(HttpServletRequest request, HttpServletResponse response) {
        // 쿠키에서 refresh 토큰 추출
        String refreshToken = jwtCookieManager.getRefreshTokenFromCookie(request);
        if (refreshToken != null) {
            refreshTokenService.deleteByEmployeeId(
                    jwtTokenProvider.getEmployeeId(refreshToken)
            );
        }
        jwtCookieManager.removeAccessTokenCookie(response);
        jwtCookieManager.removeRefreshTokenCookie(response);
        return ResponseEntity.ok().build();
    }

}
