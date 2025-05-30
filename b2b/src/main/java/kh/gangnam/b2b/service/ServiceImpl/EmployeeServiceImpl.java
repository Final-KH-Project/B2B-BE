package kh.gangnam.b2b.service.ServiceImpl;

import kh.gangnam.b2b.dto.employee.EmployeeDTO;
import kh.gangnam.b2b.dto.employee.request.PasswordChangeRequest;
import kh.gangnam.b2b.entity.auth.Employee;
import kh.gangnam.b2b.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class EmployeeServiceImpl {

    private final EmployeeRepository employeeRepository;
    private final PasswordEncoder passwordEncoder;

    // 프로필 조회
    public EmployeeDTO getEmployeeInfoByEmployeeId(Long employeeId) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        return EmployeeDTO.fromEntity(employee);
    }
    // 패스워드 변경
    public void updatePassword(Long employeeId, PasswordChangeRequest request) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        // 현재 비밀번호가 맞는지 확인
        if (!passwordEncoder.matches(request.getPrePassword(), employee.getPassword())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "현재 비밀번호가 맞지 않습니다.");
        }

        employee.setPassword(passwordEncoder.encode(request.getNewPassword()));
        employeeRepository.save(employee);
    }
    // 부서 변경
    public void updateDepartment(Long employeeId, String department) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        employee.setDepartment(department);
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
    public void updateProfileImage(Long employeeId, MultipartFile file) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        employeeRepository.save(employee);
    }
}
