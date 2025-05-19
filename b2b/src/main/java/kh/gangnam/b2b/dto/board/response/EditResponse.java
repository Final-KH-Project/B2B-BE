package kh.gangnam.b2b.dto.board.response;

import kh.gangnam.b2b.dto.board.request.EmployeeResponse;
import kh.gangnam.b2b.entity.board.Board;
import kh.gangnam.b2b.entity.board.BoardType;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class EditResponse {
    private String title;
    private String content;
    private BoardType type;

    private EmployeeResponse author;

    public static EditResponse fromEntity(Board board,String content){
        return  EditResponse.builder()
                .title(board.getTitle())
                .content(content)
                .type(board.getType())
                .author(EmployeeResponse.fromEntity(board.getAuthor()))
                .build();
    }
}
