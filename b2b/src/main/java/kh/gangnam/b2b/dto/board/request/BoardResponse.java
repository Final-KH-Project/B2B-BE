package kh.gangnam.b2b.dto.board.request;

import kh.gangnam.b2b.entity.board.Board;
import kh.gangnam.b2b.entity.board.BoardType;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class BoardResponse {
    private Long boardId;
    private String title;
    private String content;
    private BoardType type;

    private UserResponse  author;

    public static BoardResponse fromEntity(Board board){
        return  BoardResponse.builder()
                .boardId(board.getBoardId())
                .title(board.getTitle())
                .content(board.getContent())
                .type(board.getType())
                .author(UserResponse.fromEntity(board.getAuthor()))
                .build();
    }

}
