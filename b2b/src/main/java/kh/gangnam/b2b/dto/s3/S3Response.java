package kh.gangnam.b2b.dto.s3;

import kh.gangnam.b2b.entity.board.Board;
import kh.gangnam.b2b.entity.board.BoardImage;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class S3Response {
    private String url; // http로 접근가능한 주소
    private String bucketKey;

    public BoardImage toEntity(Board board) {
        return BoardImage.builder()
                .s3Path(this.url)
                .board(board)
                .build();
    }
}
