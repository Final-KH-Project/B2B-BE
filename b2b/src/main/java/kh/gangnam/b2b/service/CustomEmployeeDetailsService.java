package kh.gangnam.b2b.service;

import kh.gangnam.b2b.dto.auth.CustomEmployeeDetails;
import kh.gangnam.b2b.entity.auth.Employee;
import kh.gangnam.b2b.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomEmployeeDetailsService implements UserDetailsService {

    private final EmployeeRepository employeeRepository;


    @Override
    public UserDetails loadUserByUsername(String loginId) throws UsernameNotFoundException {

        Employee employeeData = employeeRepository.findByLoginId(loginId);

        if (employeeData != null) {

            return new CustomEmployeeDetails(employeeData);
        }
        throw new UsernameNotFoundException("User not found");
    }
}
