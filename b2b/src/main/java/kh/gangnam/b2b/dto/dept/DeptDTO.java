package kh.gangnam.b2b.dto.dept;

import kh.gangnam.b2b.entity.Dept;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeptDTO {
    private Long deptId;
    private String deptName;
    private String location;
    private Long headId;          // 부서장 ID
    private String headName;      // 부서장 이름 (옵션)
    private Long parentDeptId;    // 상위 부서 ID
    private String parentDeptName;// 상위 부서명 (옵션)

    // 엔티티 → DTO 변환 메서드
    public static DeptDTO fromEntity(Dept dept) {
        return DeptDTO.builder()
                .deptId(dept.getDeptId())
                .deptName(dept.getDeptName())
                .location(dept.getLocation())
                .headId(dept.getHead() != null ? dept.getHead().getEmployeeId() : null)
                .headName(dept.getHead() != null ? dept.getHead().getName() : null)
                .parentDeptId(dept.getParentDept() != null ? dept.getParentDept().getDeptId() : null)
                .parentDeptName(dept.getParentDept() != null ? dept.getParentDept().getDeptName() : null)
                .build();
    }
}