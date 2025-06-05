package kh.gangnam.b2b.dto.board.response;

import kh.gangnam.b2b.dto.board.request.EmployeeResponse;
import kh.gangnam.b2b.entity.board.Comment;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class CommentSaveResponse {

    private Long commentId;
    private String comment;
    private Long boardId;
    private Long parent;
    private Boolean isAuthor;
    private EmployeeResponse author;
    private LocalDateTime createdDate;
    private LocalDateTime updatedAt;

    public static CommentSaveResponse fromEntity(Comment comment,Long employeeId){
        return CommentSaveResponse.builder()
                .commentId(comment.getCommentId())
                .comment(comment.getComment())
                .boardId((comment.getBoard() != null) ? comment.getBoard().getBoardId() : null)
                .parent((comment.getParent() != null) ? comment.getParent().getCommentId() : null)
                .isAuthor(comment.getAuthor().getEmployeeId() == employeeId)
                .author(EmployeeResponse.fromEntity(comment.getAuthor()))
                .createdDate(comment.getCreatedDate())
                .updatedAt(comment.getUpdatedAt())
                .build();
    }

    public static CommentSaveResponse fromEntity(Comment comment){
        return CommentSaveResponse.builder()
                .commentId(comment.getCommentId())
                .comment(comment.getComment())
                .boardId((comment.getBoard() != null) ? comment.getBoard().getBoardId() : null)
                .parent(comment.getParent() != null ? comment.getParent().getCommentId() : null)
                .author(EmployeeResponse.fromEntity(comment.getAuthor()))
                .createdDate(comment.getCreatedDate())
                .updatedAt(comment.getUpdatedAt())
                .build();
    }
}
