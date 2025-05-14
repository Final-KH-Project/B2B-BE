package kh.gangnam.b2b.dto.board.request;

import kh.gangnam.b2b.entity.board.Board;
import kh.gangnam.b2b.entity.board.BoardType;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class BoardSaveResponse {
    private Long boardId;
    private String title;
    private String content;
    private BoardType type;

    private Long  authorId;

    public static BoardSaveResponse fromEntity(Board board){
        return  BoardSaveResponse.builder()
                .boardId(board.getBoardId())
                .title(board.getTitle())
                .content(board.getContent())
                .type(board.getType())
                .authorId(board.getAuthor().getUserId())
                .build();
    }

}
