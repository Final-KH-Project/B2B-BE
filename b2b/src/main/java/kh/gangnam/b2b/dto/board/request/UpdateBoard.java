package kh.gangnam.b2b.dto.board.request;

import kh.gangnam.b2b.entity.auth.User;
import kh.gangnam.b2b.entity.board.NoticeBoard;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@NoArgsConstructor
public class UpdateBoard {

    private String title;
    private String content;
    private String postType;
    private Long postId;
    private List<String> imageUrls;

}
