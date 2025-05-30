package kh.gangnam.b2b.dto.dept;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeptCreateRequest {
    private String deptName;
    private String location;
    // 상위 부서 ID (nullable)
    private Long parentDeptId;
    // 부서장 EmployeeId (nullables)
    private Long headId;
}
