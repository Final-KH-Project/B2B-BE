package kh.gangnam.b2b.controller;

import kh.gangnam.b2b.dto.employee.EmployeeDTO;
import kh.gangnam.b2b.config.security.CustomEmployeeDetails;
import kh.gangnam.b2b.dto.employee.request.*;
import kh.gangnam.b2b.service.ServiceImpl.EmployeeServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/employee")
public class EmployeeController {

    private final EmployeeServiceImpl employeeService;

    // 마이페이지: 현재 로그인한 사용자 정보 조회
    @GetMapping("/mypage")
    public ResponseEntity<EmployeeDTO> myPage(@AuthenticationPrincipal CustomEmployeeDetails userDetails) {
        Long employeeId = userDetails.getEmployeeId();
        EmployeeDTO employeeDto = employeeService.getEmployeeInfoByEmployeeId(employeeId);
        return ResponseEntity.ok(employeeDto);
    }
    // 패스워드 변경
    @PostMapping("/update/password")
    public ResponseEntity<Void> updatePassword(
            @RequestBody PasswordChangeRequest request,
            @AuthenticationPrincipal CustomEmployeeDetails userDetails) {
        employeeService.updatePassword(userDetails.getEmployeeId(), request);
        return ResponseEntity.ok().build();
    }
    // 사용자 정보 변경
    @PostMapping("/update/profile")
    public EmployeeDTO updateProfile(
        @RequestBody UpdateProfileRequest request,
        @AuthenticationPrincipal CustomEmployeeDetails details) {
        return employeeService.updateProfile(details.getEmployeeId(), request);
    }

    // 부서 변경
    @PatchMapping("/department")
    public ResponseEntity<Void> updateDepartment(
            @RequestBody DepartmentUpdateRequest request,
            @AuthenticationPrincipal CustomEmployeeDetails details) {
        employeeService.updateDepartment(details.getEmployeeId(), request.getDepartment());
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
    @PostMapping("/update/profile-img")
    public ResponseEntity<Void> updateProfileImage(
            @RequestParam("file") MultipartFile file,
            @AuthenticationPrincipal CustomEmployeeDetails userDetails) {
        employeeService.updateProfileImage(userDetails.getEmployeeId(), file);
        return ResponseEntity.ok().build();
    }
}
