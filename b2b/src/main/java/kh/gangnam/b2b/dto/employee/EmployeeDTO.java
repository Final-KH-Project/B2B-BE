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
    private String manager;
    private String position;
    private String dateOfBirth;
    private String phoneNumber;
    private String address;
    private LocalDateTime createdDate;
    private String role;
    private Long employeeId;
    private Long headId; //추가 , 부서장 employeeID
    private boolean isHead; //추가, 부서장 여부확인


    public static EmployeeDTO fromEntity(Employee employee) {
        return EmployeeDTO.builder()
                .employeeId(employee.getEmployeeId())
                .loginId(employee.getLoginId())
                .name(employee.getName())
                .manager(employee.getManager() !=null ? employee.getManager().getName() : null)
                .department(employee.getDept() != null ? employee.getDept().getDeptName() : null)
                .profile(employee.getProfile())
                .position(employee.getPosition().getKrName())
                .dateOfBirth(employee.getDateOfBirth())
                .phoneNumber(employee.getPhoneNumber())
                .address(employee.getAddress())
                .createdDate(employee.getCreatedDate())
                .role(employee.getRole())
                .headId(employee.getDept() != null
                        ? employee.getDept().getHead().getEmployeeId() : null) //추가
                .isHead(employee.getDept() != null && employee.getDept().getHead() != null) //추가
                .build();
    }
}
