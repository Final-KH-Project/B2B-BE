package kh.gangnam.b2b.dto.project.response;

import kh.gangnam.b2b.entity.Dept;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class departmentListResponse {

    private Long departmentId;
    private String departmentName;

    public static departmentListResponse fromEntity(Dept dept){
        return departmentListResponse.builder().departmentId(dept.getDeptId())
                .departmentName(dept.getDeptName()).build();
    }
}
