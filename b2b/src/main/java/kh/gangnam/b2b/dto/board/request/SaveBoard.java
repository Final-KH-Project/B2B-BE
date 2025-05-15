package kh.gangnam.b2b.dto.board.request;

import kh.gangnam.b2b.entity.auth.Employee;
import kh.gangnam.b2b.entity.board.NoticeBoard;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class SaveBoard {

    private String title;
    private String content;
    private String postType;
    private List<String> imageUrls;

    public NoticeBoard toEntity(Employee employee) {
        return NoticeBoard.builder()
                .title(this.title)
                .content(this.content)
                .employee(employee)
                .build();
    }
}
