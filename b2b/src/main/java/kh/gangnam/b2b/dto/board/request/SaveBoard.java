package kh.gangnam.b2b.dto.board.request;

import kh.gangnam.b2b.entity.auth.User;
import kh.gangnam.b2b.entity.board.NoticeBoard;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class SaveBoard {

    private String title;
    private String content;
    private String boardType;
    private List<String> imageUrls;

    public NoticeBoard toEntity(User user) {
        return NoticeBoard.builder()
                .title(this.title)
                .content(this.content)
                .user(user)
                .build();
    }
}
