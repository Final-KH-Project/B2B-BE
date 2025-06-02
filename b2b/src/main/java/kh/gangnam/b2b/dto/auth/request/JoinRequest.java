package kh.gangnam.b2b.dto.auth.request;

import kh.gangnam.b2b.entity.auth.Employee;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
public class JoinRequest {

    private String loginId;
    private String password;
    private String name;
    private String profile;
    private String department;
    private String position;
    private String dateOfBirth;
    private String address;
    private String phoneNumber;


    public Employee toEntity(String encodedPassword, String role) {
        return Employee.builder()
                .loginId(this.loginId)
                .password(encodedPassword)
                .name(this.name)
                .profile(this.profile)
                .department(this.department)
                .position(this.position)
                .address(this.address)
                .dateOfBirth(this.dateOfBirth)
                .phoneNumber(this.phoneNumber)
                .role(role)
                .build();
    }
}
