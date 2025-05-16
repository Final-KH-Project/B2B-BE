package kh.gangnam.b2b.service.serviceImpl;

import kh.gangnam.b2b.dto.auth.request.JoinDTO;
import kh.gangnam.b2b.entity.auth.Employee;
import kh.gangnam.b2b.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final EmployeeRepository employeeRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    /**
     * 회원 가입 로직
     * @param joinDTO
     * @return
     * 이미 존재하는 이름일 경우 (409)
     * 가입 성공 (200)
     */
    public ResponseEntity<String> joinProcess(JoinDTO joinDTO) {

        String loginId = joinDTO.getLoginId();
        String password = joinDTO.getPassword();

        Boolean isExist = employeeRepository.existsByLoginId(loginId);

        if (isExist) {

            return ResponseEntity.status(HttpStatus.CONFLICT).body(loginId + "는 이미 존재하는 아이디입니다.");
        }

        // 비밀번호 인코딩
        String encodedPassword = bCryptPasswordEncoder.encode(password);

        // User 엔티티로 변환
        Employee employee = joinDTO.toEntity(encodedPassword, "ROLE_ADMIN");

        employeeRepository.save(employee);

        return ResponseEntity.ok(loginId + " 회원가입 완료");
    }
}
