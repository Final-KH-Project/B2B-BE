package kh.gangnam.b2b.dto.board.request;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateBoard {

    // 게시글 업데이트 요청 DTO
    // 게시글 엔티티 필드가 존재해야 함

    private String title;
    private String content;
}
