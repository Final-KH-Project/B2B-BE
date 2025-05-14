package kh.gangnam.b2b.dto.board.request;

import kh.gangnam.b2b.entity.board.AnonymousBoard;
import kh.gangnam.b2b.entity.board.EventBoard;
import kh.gangnam.b2b.entity.board.FreeBoard;
import kh.gangnam.b2b.entity.board.NoticeBoard;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SaveBoard {
    // 게시글 저장 요청 DTO
    // 게시글 엔티티 필드가 존재해야 함

    private String title;
    private String content;
    private String department;
    private String author;

    public NoticeBoard toNoticeEntity() {
        return new NoticeBoard(title, content, department, author);
    }
    public EventBoard toBoardEntity() {
        return new EventBoard(title, content, department, author);
    }
    public AnonymousBoard toAnonymousBoard ()  {
        return new AnonymousBoard(title, content);
    }
    public FreeBoard toFreeBoard () {
        return new FreeBoard(title, content, department, author);
    }
}
