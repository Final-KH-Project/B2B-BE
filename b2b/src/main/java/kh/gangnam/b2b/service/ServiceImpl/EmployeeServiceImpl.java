package kh.gangnam.b2b.service.ServiceImpl;

import kh.gangnam.b2b.dto.employee.EmployeeDTO;
import kh.gangnam.b2b.dto.employee.request.PasswordChangeRequest;
import kh.gangnam.b2b.dto.employee.request.UpdateProfileRequest;
import kh.gangnam.b2b.dto.s3.S3Response;
import kh.gangnam.b2b.entity.auth.Employee;
import kh.gangnam.b2b.repository.EmployeeRepository;
import kh.gangnam.b2b.util.S3ServiceUtil;
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
    private final S3ServiceUtil s3ServiceUtil;

    // 프로필 조회
    public EmployeeDTO getEmployeeInfoByEmployeeId(Long employeeId) {
        Employee employee = validEmployee(employeeId);
        return EmployeeDTO.fromEntity(employee);
    }
    // 패스워드 변경
    public void updatePassword(Long employeeId, PasswordChangeRequest request) {
        Employee employee = validEmployee(employeeId);
        // 현재 비밀번호가 맞는지 확인
        if (!passwordEncoder.matches(request.getPrePassword(), employee.getPassword())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "현재 비밀번호가 맞지 않습니다.");
        }

        employee.setPassword(passwordEncoder.encode(request.getNewPassword()));
        employeeRepository.save(employee);
    }

    // 사용자 프로필 변경
    public EmployeeDTO updateProfile(Long employeeId, UpdateProfileRequest request) {
        Employee employee = validEmployee(employeeId);

        employee.updateProfile(request);
        employeeRepository.save(employee);
        return EmployeeDTO.fromEntity(employee);
    }

    // 부서 변경
    public void updateDepartment(Long employeeId, String department) {
        Employee employee = validEmployee(employeeId);
        employee.setDepartment(department);
        employeeRepository.save(employee);
    }
    // 직급 변경
    public void updatePosition(Long employeeId, String position) {
        Employee employee = validEmployee(employeeId);
        employee.setPosition(position);
        employeeRepository.save(employee);
    }
    // 전화번호 변경
    public void updatePhoneNumber(Long employeeId, String phoneNumber) {
        Employee employee = validEmployee(employeeId);
        employee.setPhoneNumber(phoneNumber);
        employeeRepository.save(employee);
    }
    // 프로필 이미지 변경
    public void updateProfileImage(Long employeeId, MultipartFile file) {
        Employee employee = validEmployee(employeeId);

        // S3에 프로필 이미지 업로드 (기존 파일 자동 삭제 포함)
        S3Response s3Response = s3ServiceUtil.uploadProfileImage(file, employeeId);

        // 프로필 URL 업데이트
        employee.setProfile(s3Response.getUrl());
        employeeRepository.save(employee);
    }

    private Employee validEmployee(Long employeeId) {
        return employeeRepository.findById(employeeId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
    }
}
