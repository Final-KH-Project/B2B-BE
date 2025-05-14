package kh.gangnam.b2b.dto.board.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import kh.gangnam.b2b.entity.board.AnonymousBoard;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class ReadAnonymous {

    private Long id;
    private String title;
    private String content;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createAt;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;

    public ReadAnonymous(AnonymousBoard board) {
        this.id = board.getAnonymousId();
        this.title = board.getTitle();
        this.content = board.getContent();
        this.createAt = board.getCreatedDate();
        this.updatedAt = board.getUpdatedAt();
    }
}
