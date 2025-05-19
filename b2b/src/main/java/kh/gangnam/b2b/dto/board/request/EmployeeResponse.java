package kh.gangnam.b2b.dto.board.request;

import kh.gangnam.b2b.entity.auth.Employee;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class EmployeeResponse {
    private Long authorId;
    private String loginId;
    private String name;

    public static EmployeeResponse fromEntity(Employee author){
        return EmployeeResponse.builder()
                .authorId(author.getEmployeeId())
                .loginId(author.getLoginId())
                .name(author.getName())
                .build();
    }
}
