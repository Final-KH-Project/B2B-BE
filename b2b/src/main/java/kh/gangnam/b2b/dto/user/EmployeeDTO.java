package kh.gangnam.b2b.dto.user;

import kh.gangnam.b2b.entity.auth.Employee;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EmployeeDTO {
    private Long employeeId;
    // 로그인 id
    private String loginId;
    private String name;
    private String profile;
    private String department;
    private String position;
    private String dateOfBirth;
    private String phoneNumber;
    private String role;

    public static EmployeeDTO fromEntity(Employee employee) {
        return EmployeeDTO.builder()
                .employeeId(employee.getEmployeeId())
                .loginId(employee.getLoginId())
                .name(employee.getName())
                .profile(employee.getProfile())
                .department(employee.getDepartment())
                .position(employee.getPosition())
                .dateOfBirth(employee.getDateOfBirth())
                .phoneNumber(employee.getPhoneNumber())
                .role(employee.getRole())
                .build();
    }
}
