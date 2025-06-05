package kh.gangnam.b2b.dto.board.request;

import kh.gangnam.b2b.entity.auth.Employee;
import kh.gangnam.b2b.entity.board.Board;
import kh.gangnam.b2b.entity.board.Comment;

public record CommentSaveRequest(String comment,
                                 Long boardId,
                                 Long parentId) {
    public Comment toEntity(Board board, Employee employee, Comment parent){

        return Comment.builder()
                .comment(comment).board(board)
                .author(employee).parent(parent)
                .build();
    }
}
