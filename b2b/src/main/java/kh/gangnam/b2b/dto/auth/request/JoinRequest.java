package kh.gangnam.b2b.dto.auth.request;

import kh.gangnam.b2b.dto.employee.Position;
import kh.gangnam.b2b.entity.auth.Employee;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
@Builder
public class JoinRequest {

    private String loginId;
    private String password;
    private String name;
    private String dateOfBirth;
    private String address;
    private String phoneNumber;


    public Employee toEntity(String encodedPassword, String role) {
        return Employee.builder()
                .loginId(this.loginId)
                .password(encodedPassword)
                .name(this.name)
                .address(this.address)
                .position(Position.NEWBIE)
                .dateOfBirth(this.dateOfBirth)
                .phoneNumber(this.phoneNumber)
                .role(role)
                .build();
    }
}
