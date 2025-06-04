package kh.gangnam.b2b.dto.dept;

import lombok.Getter;

@Getter
public class MoveEmployeeToDeptRequest {

    private Long employeeId;
    private Long deptId;
}
