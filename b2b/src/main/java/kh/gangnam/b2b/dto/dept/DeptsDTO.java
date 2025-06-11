package kh.gangnam.b2b.dto.dept;

import kh.gangnam.b2b.entity.Dept;
import lombok.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeptsDTO {
    private Long id;
    private String name;
    private String department;
    private int level;
    private Long parentId;
    @Builder.Default
    private List<DeptsDTO> children = new ArrayList<>();

    // Dept 엔티티 → DeptsDTO 변환
    public static DeptsDTO fromEntity(Dept dept, int level) {
        return DeptsDTO.builder()
                .id(dept.getDeptId())
                .name(dept.getDeptName())
                .department(dept.getDeptName())
                .level(level)
                .parentId(dept.getParentDept() != null ? dept.getParentDept().getDeptId() : null)
                .build();
    }

    // Dept 리스트 → 트리 구조 DeptsDTO 리스트 변환
    public static List<DeptsDTO> buildDeptsTree(List<Dept> depts) {
        Map<Long, DeptsDTO> map = new HashMap<>();
        List<DeptsDTO> roots = new ArrayList<>();

        for (Dept dept : depts) {
            map.put(dept.getDeptId(), fromEntity(dept, 1));
        }

        for (Dept dept : depts) {
            DeptsDTO dto = map.get(dept.getDeptId());
            Long parentId = dto.getParentId();
            if (parentId == null) {
                roots.add(dto);
            } else {
                DeptsDTO parent = map.get(parentId);
                if (parent != null) {
                    dto.setLevel(parent.getLevel() + 1);
                    parent.getChildren().add(dto);
                }
            }
        }
        return roots;
    }
}
