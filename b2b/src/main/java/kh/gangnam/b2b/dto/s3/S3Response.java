package kh.gangnam.b2b.dto.s3;

import kh.gangnam.b2b.entity.board.BoardImage;
import kh.gangnam.b2b.entity.board.NoticeBoard;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class S3Response {
    private String url; // http로 접근가능한 주소
    private String bucketKey;

    public BoardImage toEntity(NoticeBoard board) {
        return BoardImage.builder()
                .s3Path(this.url)
                .board(board)
                .build();
    }
}
