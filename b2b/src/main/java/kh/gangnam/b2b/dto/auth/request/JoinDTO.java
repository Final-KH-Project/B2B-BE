package kh.gangnam.b2b.dto.auth.request;

import kh.gangnam.b2b.entity.auth.Employee;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class JoinDTO {

    private String loginId;
    private String password;
    private String name;
    private String profile;
    private String department;
    private String position;
    private String dateOfBirth;
    private String phoneNumber;


    public Employee toEntity(String encodedPassword, String role) {
        Employee employee = new Employee();
        employee.setLoginId(this.loginId);
        // 비밀번호는 인코딩된 값 사용
        employee.setPassword(encodedPassword);
        employee.setName(this.name);
        employee.setProfile(this.profile);
        employee.setDepartment(this.department);
        employee.setPosition(this.position);
        employee.setDateOfBirth(this.dateOfBirth);
        employee.setPhoneNumber(this.phoneNumber);
        employee.setRole(role);
        return employee;
    }
}
