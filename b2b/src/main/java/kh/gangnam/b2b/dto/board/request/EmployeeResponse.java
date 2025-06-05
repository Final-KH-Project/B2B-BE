package kh.gangnam.b2b.dto.board.request;

import kh.gangnam.b2b.entity.auth.Employee;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor //알림 메세지
@AllArgsConstructor //알림 메세지 생성으로 추가
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
