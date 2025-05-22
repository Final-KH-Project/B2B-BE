package kh.gangnam.b2b.controller;

import kh.gangnam.b2b.dto.auth.request.RoleRequest;
import kh.gangnam.b2b.dto.employee.EmployeeDTO;
import kh.gangnam.b2b.dto.auth.CustomEmployeeDetails;
import kh.gangnam.b2b.dto.employee.request.*;
import kh.gangnam.b2b.service.ServiceImpl.EmployeeServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/user")
public class EmployeeController {

    private final EmployeeServiceImpl employeeService;

    // 마이페이지: 현재 로그인한 사용자 정보 조회
    @GetMapping("/profile")
    public ResponseEntity<EmployeeDTO> myPage(@AuthenticationPrincipal CustomEmployeeDetails userDetails) {
        // userDetails는 JWTFilter에서 인증된 사용자 정보
        Long employeeId = userDetails.getEmployeeId();
        EmployeeDTO employeeDto = employeeService.getEmployeeInfoByEmployeeId(employeeId);

        return ResponseEntity.ok(employeeDto);
    }

    // 패스워드 변경
    @PostMapping("/password")
    public ResponseEntity<Void> updatePassword(
            @RequestBody PasswordChangeRequest request,
            @AuthenticationPrincipal CustomEmployeeDetails userDetails) {
        employeeService.updatePassword(userDetails.getEmployeeId(), request.getNewPassword());
        return ResponseEntity.ok().build();
    }

    // 부서 변경
    @PatchMapping("/department")
    public ResponseEntity<Void> updateDepartment(
            @RequestBody DepartmentUpdateRequest request,
            @AuthenticationPrincipal CustomEmployeeDetails userDetails) {
        employeeService.updateDepartment(userDetails.getEmployeeId(), request.getDepartment());
        return ResponseEntity.ok().build();
    }

    // 직급 변경
    @PatchMapping("/position")
    public ResponseEntity<Void> updatePosition(
            @RequestBody PositionUpdateRequest request,
            @AuthenticationPrincipal CustomEmployeeDetails userDetails) {
        employeeService.updatePosition(userDetails.getEmployeeId(), request.getPosition());
        return ResponseEntity.ok().build();
    }

    // 전화번호 변경
    @PatchMapping("/phone")
    public ResponseEntity<Void> updatePhoneNumber(
            @RequestBody PhoneUpdateRequest request,
            @AuthenticationPrincipal CustomEmployeeDetails userDetails) {
        employeeService.updatePhoneNumber(userDetails.getEmployeeId(), request.getPhoneNumber());
        return ResponseEntity.ok().build();
    }

    // 프로필 이미지 변경
    @PatchMapping("/profile-image")
    public ResponseEntity<Void> updateProfileImage(
            @RequestBody ProfileImageRequest request,
            @AuthenticationPrincipal CustomEmployeeDetails userDetails) {
        employeeService.updateProfileImage(userDetails.getEmployeeId(), request.getProfileImageUrl());
        return ResponseEntity.ok().build();
    }
    // Role 변경
    @PostMapping("/update/role")
    public ResponseEntity<String> updateRole(@RequestBody RoleRequest request) {
        return employeeService.updateRole(request);
    }
}
