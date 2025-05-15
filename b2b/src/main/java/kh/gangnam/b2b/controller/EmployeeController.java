package kh.gangnam.b2b.controller;

import kh.gangnam.b2b.dto.user.EmployeeDTO;
import kh.gangnam.b2b.dto.auth.CustomEmployeeDetails;
import kh.gangnam.b2b.dto.user.request.*;
import kh.gangnam.b2b.service.ServiceImpl.EmployeeServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
public class EmployeeController {

    private final EmployeeServiceImpl userService;

    // 마이페이지: 현재 로그인한 사용자 정보 조회
    @GetMapping("/profile")
    public ResponseEntity<EmployeeDTO> myPage(@AuthenticationPrincipal CustomEmployeeDetails userDetails) {
        // userDetails는 JWTFilter에서 인증된 사용자 정보
        Long employeeId = userDetails.getEmployeeId();
        EmployeeDTO employeeDto = userService.getEmployeeInfoByEmployeeId(employeeId);
        return ResponseEntity.ok(employeeDto);
    }
    // 패스워드 변경
    @PostMapping("/password")
    public ResponseEntity<Void> updatePassword(
            @RequestBody PasswordChangeRequest request,
            @AuthenticationPrincipal CustomEmployeeDetails userDetails) {
        userService.updatePassword(userDetails.getEmployeeId(), request.getNewPassword());
        return ResponseEntity.ok().build();
    }
    // 부서 변경
    @PatchMapping("/department")
    public ResponseEntity<Void> updateDepartment(
            @RequestBody DepartmentUpdateRequest request,
            @AuthenticationPrincipal CustomEmployeeDetails userDetails) {
        userService.updateDepartment(userDetails.getEmployeeId(), request.getDepartment());
        return ResponseEntity.ok().build();
    }
    // 직급 변경
    @PatchMapping("/position")
    public ResponseEntity<Void> updatePosition(
            @RequestBody PositionUpdateRequest request,
            @AuthenticationPrincipal CustomEmployeeDetails userDetails) {
        userService.updatePosition(userDetails.getEmployeeId(), request.getPosition());
        return ResponseEntity.ok().build();
    }
    // 전화번호 변경
    @PatchMapping("/phone")
    public ResponseEntity<Void> updatePhoneNumber(
            @RequestBody PhoneUpdateRequest request,
            @AuthenticationPrincipal CustomEmployeeDetails userDetails) {
        userService.updatePhoneNumber(userDetails.getEmployeeId(), request.getPhoneNumber());
        return ResponseEntity.ok().build();
    }
    // 프로필 이미지 변경
    @PatchMapping("/profile-image")
    public ResponseEntity<Void> updateProfileImage(
            @RequestBody ProfileImageRequest request,
            @AuthenticationPrincipal CustomEmployeeDetails userDetails) {
        userService.updateProfileImage(userDetails.getEmployeeId(), request.getProfileImageUrl());
        return ResponseEntity.ok().build();
    }
}
