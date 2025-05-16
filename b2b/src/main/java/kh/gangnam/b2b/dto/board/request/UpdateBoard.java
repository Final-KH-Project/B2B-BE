package kh.gangnam.b2b.dto.board.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class UpdateBoard {

    private String title;
    private String content;
    private String boardType;
    private Long postId;
    private List<String> imageUrls;

}
