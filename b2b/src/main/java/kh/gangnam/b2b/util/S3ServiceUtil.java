package kh.gangnam.b2b.util;

import io.awspring.cloud.s3.ObjectMetadata;
import io.awspring.cloud.s3.S3Resource;
import io.awspring.cloud.s3.S3Template;
import kh.gangnam.b2b.dto.s3.S3Response;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.CopyObjectRequest;
import software.amazon.awssdk.services.s3.model.CopyObjectResponse;
import software.amazon.awssdk.services.s3.model.ObjectCannedACL;
import software.amazon.awssdk.services.s3.model.S3Exception;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class S3ServiceUtil {

    private final S3Template s3Template;
    private final S3Client s3Client;

    // application.yml에서 설정된 S3 버킷 이름
    @Value("${spring.cloud.aws.s3.bucket}")
    private String BUCKET_NAME;

    // 임시 파일 저장 경로
    @Value("${spring.cloud.aws.s3.temp}")
    private String TEMP_PATH;

    // 실제 파일 저장 경로
    @Value("${spring.cloud.aws.s3.upload}")
    private String UPLOAD_PATH;

    //파일이름 nanoTime()을 이용하여 변경
    private static String newFileNameByNanotime(String orgName) {
        int idx=orgName.lastIndexOf(".");
        return orgName.substring(0, idx)+"-"+(System.nanoTime()/1000000)
                + orgName.substring(idx); //.확장자 :  .jpg
    }

    /**
     * 임시 폴더에 파일을 업로드합니다.
     *
     * @param file   업로드할 파일
     * @return 업로드된 파일의 S3 URL
     * @throws RuntimeException 파일 업로드 실패 시 발생
     */
    public S3Response uploadToTemp(MultipartFile file) {
        try {
            // 임시 파일 경로 생성 (사용자PK/파일명) // temp/101/
            //String tempKey = TEMP_PATH + userPk + "/" + createUniqueFileName(file.getOriginalFilename());
            String tempKey = TEMP_PATH + newFileNameByNanotime(file.getOriginalFilename());

            // 메타데이터 설정 (Content-Type, 퍼블릭 액세스 등)
            ObjectMetadata metadata = ObjectMetadata.builder().contentType(file.getContentType())
                    .acl(ObjectCannedACL.PUBLIC_READ).build();

            // S3에 파일 업로드
            S3Resource s3Resource = s3Template.upload(BUCKET_NAME, tempKey, file.getInputStream(), metadata);
            //return s3Resource.getURL().toString().substring(6);
            return S3Response.builder()
                    .url(s3Resource.getURL().toString())
                    .bucketKey(tempKey)
                    .build();
        } catch (IOException e) {
            throw new RuntimeException("임시 파일 업로드 실패", e);
        }
    }

    /**
     * 임시 파일을 실제 저장 위치로 이동합니다.
     *
     * @param url 임시 파일의 URL
     * @param postId  사용자 PK
     * @return 이동된 파일의 S3FileResponse
     */

    public S3Response moveFromTempToUpload(String url, Long postId) {

        // tempUrl에서 키 추출
        String prefix = "https://s3.ap-northeast-2.amazonaws.com/com.kh.cjh.bucket/";
        String bucketKey = url.replace(prefix, "");
        String uploadKey = UPLOAD_PATH + postId + url.substring(url.lastIndexOf("/"));

        // 파일 이동(tempKey, uploadKey)
        return moveFile(bucketKey, uploadKey);
    }

    public S3Response moveFile(String sourceKey, String destinationKey) {
        try {
            CopyObjectRequest copyObjectRequest = CopyObjectRequest.builder()
                    .sourceBucket(BUCKET_NAME)//원본의 버킷이름
                    .sourceKey(sourceKey)//원본의 키->temp/폴더의 키
                    .destinationBucket(BUCKET_NAME)//복사할 버킷이름
                    .destinationKey(destinationKey)//upload/폴더의 키
                    .acl(ObjectCannedACL.PUBLIC_READ)//로그인하지 않아도 이미지를 볼 수 있도록
                    .build();

            CopyObjectResponse result = s3Client.copyObject(copyObjectRequest);

            if (result != null) {
                //복사가 이루어지면 temp의 이미지는 제거
                s3Template.deleteObject(BUCKET_NAME, sourceKey);

                return S3Response.builder()
                        .url(s3Client.utilities()
                                .getUrl(builder -> builder.bucket(BUCKET_NAME).key(destinationKey).build()).toString())
                        .bucketKey(destinationKey)
                        .build();
            }
            throw new RuntimeException("파일 이동 실패");
        } catch (S3Exception e) {
            throw new RuntimeException("S3 파일 이동 실패", e);
        } catch (Exception e) {
            throw new RuntimeException("파일 이동 중 오류 발생", e);
        }
    }

}
