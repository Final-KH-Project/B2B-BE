package kh.gangnam.b2b.dto.project.response;

import kh.gangnam.b2b.entity.auth.Employee;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class employeeListResponse {

    private Long employeeId;
    private String employeeName;

    public static employeeListResponse fromEntity(Employee employee){
        return employeeListResponse.builder().employeeId(employee.getEmployeeId())
                .employeeName(employee.getName()).build();
    }
}
