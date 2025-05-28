package kh.gangnam.b2b.security;

import kh.gangnam.b2b.entity.auth.Employee;
import kh.gangnam.b2b.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


/**
 * Spring Security의 UserDetailsService를 구현한 커스텀 사용자 인증 서비스
 *
 * 이 서비스는 사용자 이메일을 기반으로 사용자 정보를 로드하고 Spring Security의 UserDetails 객체로 변환합니다.
 * 사용자 인증 과정에서 사용자의 이메일과 비밀번호, 권한 정보를 제공합니다.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CustomEmployeeDetailsService implements UserDetailsService {

    // 사용자 정보를 조회하기 위한 리포지토리
    private final EmployeeRepository employeeRepository;

    /**
     * 로그인 ID를  기반으로 사용자 정보를 로드하여 UserDetails 객체로 변환합니다.
     *
     * @param loginId 사용자 login ID
     * @return UserDetails 객체 (사용자 인증 정보)
     * @throws UsernameNotFoundException 사용자를 찾을 수 없는 경우 발생
     */
    @Override
    public UserDetails loadUserByUsername(String loginId) throws UsernameNotFoundException {

        // employeeId로 조회
        Employee employee = employeeRepository.findByLoginId(loginId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        return new CustomEmployeeDetails(employee);
    }
}
