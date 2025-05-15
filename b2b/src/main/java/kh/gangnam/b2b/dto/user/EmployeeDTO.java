package kh.gangnam.b2b.dto.user;

import kh.gangnam.b2b.entity.auth.Employee;
import lombok.Data;

@Data
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
        EmployeeDTO dto = new EmployeeDTO();
        dto.setEmployeeId(employee.getEmployeeId());
        dto.setLoginId(employee.getLoginId());
        dto.setName(employee.getName());
        dto.setProfile(employee.getProfile());
        dto.setDepartment(employee.getDepartment());
        dto.setPosition(employee.getPosition());
        dto.setDateOfBirth(employee.getDateOfBirth());
        dto.setPhoneNumber(employee.getPhoneNumber());
        dto.setRole(employee.getRole());
        return dto;
    }
}
