package kh.gangnam.b2b.controller;

import kh.gangnam.b2b.dto.UserDTO;
import kh.gangnam.b2b.dto.auth.CustomUserDetails;
import kh.gangnam.b2b.service.ServiceImpl.UserServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
public class UserController {

    private final UserServiceImpl userService;

    // 마이페이지: 현재 로그인한 사용자 정보 조회
    @GetMapping("/mypage")
    public ResponseEntity<UserDTO> myPage(@AuthenticationPrincipal CustomUserDetails userDetails) {
        // userDetails는 JWTFilter에서 인증된 사용자 정보
        String username = userDetails.getUsername();
        UserDTO userDto = userService.getUserInfoByUsername(username);
        return ResponseEntity.ok(userDto);
    }
}
