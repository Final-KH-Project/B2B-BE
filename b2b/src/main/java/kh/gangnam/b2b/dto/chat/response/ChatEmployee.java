package kh.gangnam.b2b.dto.chat.response;

import kh.gangnam.b2b.entity.auth.Employee;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class ChatEmployee {
    private String loginId;
    private Long employeeId;
    private String name;
    private String profile;
    private String department;
    private String manager;
    private String position;
    private String dateOfBirth;
    private String phoneNumber;
    private LocalDateTime createdDate;

    public static ChatEmployee fromEntity(Employee employee) {
        return ChatEmployee.builder()
                .loginId(employee.getLoginId())
                .employeeId(employee.getEmployeeId())
                .name(employee.getName())
                .manager(employee.getManager() != null ? employee.getManager().getName() : null)
                .department(employee.getDept() != null ? employee.getDept().getDeptName() : "부서없음")
                .profile(employee.getProfile())
                .position(employee.getPosition())
                .dateOfBirth(employee.getDateOfBirth())
                .phoneNumber(employee.getPhoneNumber())
                .createdDate(employee.getCreatedDate())
                .build();
    }
}
