package kh.gangnam.b2b.service.ServiceImpl;

import kh.gangnam.b2b.dto.auth.request.RoleRequest;
import kh.gangnam.b2b.dto.employee.EmployeeDTO;
import kh.gangnam.b2b.entity.auth.Employee;
import kh.gangnam.b2b.entity.auth.Refresh;
import kh.gangnam.b2b.repository.EmployeeRepository;
import kh.gangnam.b2b.repository.RefreshRepository;
import kh.gangnam.b2b.security.JWTUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmployeeServiceImpl {

    private final EmployeeRepository employeeRepository;
    private final RefreshRepository refreshRepository;
    private final PasswordEncoder passwordEncoder;
    private final JWTUtil jwtUtil;

    @Value("${token.refreshExpired}")
    private Long refreshExpired;

    // 프로필 조회
    public EmployeeDTO getEmployeeInfoByEmployeeId(Long employeeId) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        return EmployeeDTO.fromEntity(employee);
    }
    // 패스워드 변경
    public void updatePassword(Long employeeId, String newPassword) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        employee.setPassword(passwordEncoder.encode(newPassword));
        employeeRepository.save(employee);
    }
    // 부서 변경
    public void updateDepartment(Long employeeId, String department) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        employeeRepository.save(employee);
    }
    // 직급 변경
    public void updatePosition(Long employeeId, String position) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        employee.setPosition(position);
        employeeRepository.save(employee);
    }
    // 전화번호 변경
    public void updatePhoneNumber(Long employeeId, String phoneNumber) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        employee.setPhoneNumber(phoneNumber);
        employeeRepository.save(employee);
    }
    // 프로필 이미지 변경
    public void updateProfileImage(Long employeeId, String profileImageUrl) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        employee.setProfile(profileImageUrl);
        employeeRepository.save(employee);
    }

    public ResponseEntity<String> updateRole(RoleRequest request) {
        Refresh refresh = refreshRepository.findByEmployeeId(request.getEmployeeId());
        String token = refresh.getRefresh();
        String loginId = jwtUtil.getLoginId(token);
        String role = request.getRole();
        Long employeeId= jwtUtil.getEmployeeId(token);

        String newRefresh = jwtUtil.createJwt("refresh", loginId, employeeId, role, refreshExpired);

        refreshRepository.deleteByEmployeeId(employeeId);

        Refresh refreshEntity = Refresh.builder()
                .employeeId(employeeId)
                .refresh(newRefresh)
                .expiresAt(LocalDateTime.now().plusSeconds(refreshExpired/1000))
                .build();
        refreshRepository.save(refreshEntity);

        Employee employee = employeeRepository.findByEmployeeId(employeeId);
        employee.setPosition(request.getPosition());
        employee.setRole(request.getRole());
        employeeRepository.save(employee);

        log.info("role: {}", jwtUtil.getRole(refreshEntity.getRefresh()));

        return ResponseEntity.ok("ROLE 저장 성공");
    }
}
