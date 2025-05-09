package kh.gangnam.b2b.dto.s3;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class S3Response {
    private String url; // http로 접근가능한 주소
    private String bucketKey;
}
