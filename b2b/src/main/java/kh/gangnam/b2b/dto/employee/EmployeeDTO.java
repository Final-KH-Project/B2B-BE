package kh.gangnam.b2b.dto.employee;

import kh.gangnam.b2b.entity.auth.Employee;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class EmployeeDTO {

    private String loginId;
    private String name;
    private String profile;
    private String department;
    private String position;
    private String dateOfBirth;
    private String phoneNumber;
    private String address;
    private LocalDateTime createdDate;
    private String role;

    public static EmployeeDTO fromEntity(Employee employee) {
        return EmployeeDTO.builder()
                .loginId(employee.getLoginId())
                .name(employee.getName())
                .profile(employee.getProfile())
                .department(employee.getDepartment())
                .position(employee.getPosition())
                .dateOfBirth(employee.getDateOfBirth())
                .phoneNumber(employee.getPhoneNumber())
                .address(employee.getAddress())
                .createdDate(employee.getCreatedDate())
                .role(employee.getRole())
                .build();
    }
}
