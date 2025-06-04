package kh.gangnam.b2b.dto.dept;

import kh.gangnam.b2b.entity.Dept;
import kh.gangnam.b2b.entity.auth.Employee;
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

    public Dept toEntity(Employee head, Dept parentDept) {
        return Dept.builder()
                .deptName(this.deptName)
                .location(this.location)
                .parentDept(parentDept)
                .head(head)
                .build();
    }
}
