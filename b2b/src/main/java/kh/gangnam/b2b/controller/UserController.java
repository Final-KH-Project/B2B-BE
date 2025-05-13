package kh.gangnam.b2b.controller;

import kh.gangnam.b2b.dto.user.UserDTO;
import kh.gangnam.b2b.dto.auth.CustomUserDetails;
import kh.gangnam.b2b.dto.user.request.*;
import kh.gangnam.b2b.service.ServiceImpl.UserServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
public class UserController {

    private final UserServiceImpl userService;

    // 마이페이지: 현재 로그인한 사용자 정보 조회
    @GetMapping("/profile")
    public ResponseEntity<UserDTO> myPage(@AuthenticationPrincipal CustomUserDetails userDetails) {
        // userDetails는 JWTFilter에서 인증된 사용자 정보
        Long userId = userDetails.getUserId();
        UserDTO userDto = userService.getUserInfoByUserId(userId);
        return ResponseEntity.ok(userDto);
    }
    // 패스워드 변경
    @PostMapping("/password")
    public ResponseEntity<Void> updatePassword(
            @RequestBody PasswordChangeRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        userService.updatePassword(userDetails.getUserId(), request.getNewPassword());
        return ResponseEntity.ok().build();
    }
    // 부서 변경
    @PatchMapping("/department")
    public ResponseEntity<Void> updateDepartment(
            @RequestBody DepartmentUpdateRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        userService.updateDepartment(userDetails.getUserId(), request.getDepartment());
        return ResponseEntity.ok().build();
    }
    // 직급 변경
    @PatchMapping("/position")
    public ResponseEntity<Void> updatePosition(
            @RequestBody PositionUpdateRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        userService.updatePosition(userDetails.getUserId(), request.getPosition());
        return ResponseEntity.ok().build();
    }
    // 전화번호 변경
    @PatchMapping("/phone")
    public ResponseEntity<Void> updatePhoneNumber(
            @RequestBody PhoneUpdateRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        userService.updatePhoneNumber(userDetails.getUserId(), request.getPhoneNumber());
        return ResponseEntity.ok().build();
    }
    // 프로필 이미지 변경
    @PatchMapping("/profile-image")
    public ResponseEntity<Void> updateProfileImage(
            @RequestBody ProfileImageRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        userService.updateProfileImage(userDetails.getUserId(), request.getProfileImageUrl());
        return ResponseEntity.ok().build();
    }
}
