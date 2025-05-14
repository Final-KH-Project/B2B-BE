package kh.gangnam.b2b.dto.board.response;

import kh.gangnam.b2b.entity.board.NoticeBoard;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ReadBoard {

    private String title;
    private String content;

    public static ReadBoard fromEntity(NoticeBoard entity) {
        return new ReadBoard(
                entity.getTitle(),
                entity.getContent()
        );
    }
}
