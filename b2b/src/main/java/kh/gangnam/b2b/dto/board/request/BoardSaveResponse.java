package kh.gangnam.b2b.dto.board.request;

import kh.gangnam.b2b.entity.board.Board;
import kh.gangnam.b2b.entity.board.BoardType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor //알림 메세지
@AllArgsConstructor //알림 메세지 생성으로 추가
public class BoardSaveResponse {
    private Long boardId;
    private String title;
    private String content;
    private BoardType type;

    private Long  authorId;
    private LocalDateTime createdDate;
    private LocalDateTime updatedAt;

    public static BoardSaveResponse fromEntity(Board board){
        return BoardSaveResponse.builder()
                .boardId(board.getBoardId())
                .title(board.getTitle())
                .content(board.getContent())
                .type(board.getType())
                .authorId(board.getAuthor().getEmployeeId())
                .createdDate(board.getCreatedDate())
                .updatedAt(board.getUpdatedAt())
                .build();
    }

}
