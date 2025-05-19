package kh.gangnam.b2b.dto.board.request;

import kh.gangnam.b2b.entity.board.Board;
import kh.gangnam.b2b.entity.board.BoardType;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class BoardResponse {
    private Long boardId;
    private String title;
    private String content;
    private BoardType type;

    private EmployeeResponse author;
    private LocalDateTime createdDate;
    private LocalDateTime updatedAt;

    public static BoardResponse fromEntity(Board board){
        return  BoardResponse.builder()
                .boardId(board.getBoardId())
                .title(board.getTitle())
                .content(board.getContent())
                .type(board.getType())
                .author(EmployeeResponse.fromEntity(board.getAuthor()))
                .createdDate(board.getCreatedDate())
                .updatedAt(board.getUpdatedAt())
                .build();
    }

}
