package kh.gangnam.b2b.dto.board;

import com.fasterxml.jackson.annotation.JsonFormat;
import kh.gangnam.b2b.entity.board.EventBoard;
import kh.gangnam.b2b.entity.board.FreeBoard;
import kh.gangnam.b2b.entity.board.NoticeBoard;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class BoardDTO {

    private Read data;
    public BoardDTO(Read data) {
        this.data = data;
    }

    @Getter
    public static class Read {
        private Long id;
        private String title;
        private String content;
        private String department;
        private String author;
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime createdAt;
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime updatedAt;

        //공지 게시판
        public Read(NoticeBoard board) {
            this.id = board.getNoticeId();
            this.title = board.getTitle();
            this.content = board.getContent();
            this.department = board.getDepartment();
            this.author = board.getAuthor();
            this.createdAt = board.getCreatedDate();
            this.updatedAt = board.getUpdatedAt();
        }

        //행사 게시판
        public Read(EventBoard board) {
            this.id = board.getEventId();
            this.title = board.getTitle();
            this.content = board.getContent();
            this.department = board.getDepartment();
            this.author = board.getAuthor();
            this.createdAt = board.getCreatedDate();
            this.updatedAt = board.getUpdatedAt();
        }

        //자유 게시판
        public Read(FreeBoard board) {
            this.id = board.getFreeId();
            this.title = board.getTitle();
            this.content = board.getContent();
            this.department = board.getDepartment();
            this.author = board.getAuthor();
            this.createdAt = board.getCreatedDate();
            this.updatedAt = board.getUpdatedAt();
        }
    }
}
