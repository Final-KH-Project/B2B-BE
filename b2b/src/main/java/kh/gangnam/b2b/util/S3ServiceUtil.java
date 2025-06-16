package kh.gangnam.b2b.util;

import io.awspring.cloud.s3.ObjectMetadata;
import io.awspring.cloud.s3.S3Resource;
import io.awspring.cloud.s3.S3Template;
import kh.gangnam.b2b.dto.board.response.EditResponse;
import kh.gangnam.b2b.dto.s3.S3Response;
import kh.gangnam.b2b.entity.board.Board;
import kh.gangnam.b2b.entity.board.BoardImage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

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

    // 프로필 이미지 업로드
    public S3Response uploadProfileImage(MultipartFile file, Long employeeId) {
        // 기존 프로필 이미지 삭제 (없으면 무시)
        deleteProfileImage(employeeId);

        // 파일명: profile{employeeId}
        String key = getProfileKey(employeeId);

        try {
            // 메타데이터 설정
            ObjectMetadata metadata = ObjectMetadata.builder()
                    .contentType(file.getContentType())
                    .acl(ObjectCannedACL.PUBLIC_READ)
                    .build();

            // S3에 파일 업로드
            S3Resource s3Resource = s3Template.upload(BUCKET_NAME, key, file.getInputStream(), metadata);

            return S3Response.builder()
                    .url(s3Resource.getURL().toString())
                    .bucketKey(key)
                    .build();
        } catch (IOException e) {
            throw new RuntimeException("프로필 이미지 업로드 실패", e);
        }
    }


    // 게시글 수정 시 게시글 정보 가져오는 메서드 url 경로를 temp 로 변경
    public EditResponse editBoardUrl(Board board) {

        String content = board.getContent();

        for (BoardImage url : board.getImages()) {
            // 파일명 추출
            String originalUrl = url.getS3Path();
            String fileName = extractFileNameFromUrl(originalUrl);

            // upload/boardId -> temp/ 폴더로 복사
            S3Response tempUrl = moveFromUploadToTemp(originalUrl);

            // content 내부 URL 교체
            if (content.contains(fileName)) {
                content = content.replace(originalUrl, tempUrl.getUrl());
            }
        }
        return EditResponse.fromEntity(board, content);
    }

    public String extractFileNameFromUrl(String url) {
        return url.substring(url.lastIndexOf("/") + 1);
    }

    //파일이름 nanoTime()을 이용하여 변경
    private static String newFileNameByNanotime(String orgName) {
        int idx = orgName.lastIndexOf(".");
        return orgName.substring(0, idx) + "-" + (System.nanoTime() / 1000000)
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
            String tempKey = TEMP_PATH + newFileNameByNanotime(Objects.requireNonNull(file.getOriginalFilename()));

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
     * @param boardId  사용자 PK
     * @return 이동된 파일의 S3FileResponse
     */
    public S3Response moveFromTempToUpload(String url, Long boardId) {

        // URL에서 버킷 키 추출
        String prefix = "https://s3.ap-northeast-2.amazonaws.com/com.kh.cjh.bucket/";
        String encodedKey = url.replace(prefix, "");

        // 🔥 핵심: URL 디코딩으로 한글 복원
        String bucketKey = URLDecoder.decode(encodedKey, StandardCharsets.UTF_8);

        // 파일명만 추출
        String fileName = bucketKey.substring(bucketKey.lastIndexOf("/") + 1);
        String uploadKey = UPLOAD_PATH + boardId + "/" + fileName;

        // 파일 이동(tempKey, uploadKey)
        return moveFile(bucketKey, uploadKey);
    }

    /**
     * 게시글 이미지를 임시저장 폴더로 이동합니다.
     *
     * @param url 임시 파일의 URL
     * @return 이동된 파일의 S3FileResponse
     */
    public S3Response moveFromUploadToTemp(String url) {

        String prefix = "https://s3.ap-northeast-2.amazonaws.com/com.kh.cjh.bucket/";
        String encodedKey = url.replace(prefix, "");

        // 🔥 URL 디코딩 추가 (한글 파일명 처리)
        String bucketKey = URLDecoder.decode(encodedKey, StandardCharsets.UTF_8);

        // 파일명만 추출 (디코딩된 키에서)
        String fileName = bucketKey.substring(bucketKey.lastIndexOf("/") + 1);
        String uploadKey = TEMP_PATH + fileName;

        // 파일 이동(uploadKey, tempKey)
        return moveFile(bucketKey, uploadKey);
    }

    // s3 이미지를 다른 폴더로 이동
    public S3Response moveFile(String sourceKey, String destinationKey) {

        if (sourceKey.equals(destinationKey)) {
            System.out.println("sourceKey와 destinationKey가 같습니다. 복사 불가.");
        }

        try {
            CopyObjectRequest copyObjectRequest = CopyObjectRequest.builder()
                    .sourceBucket(BUCKET_NAME)//원본의 버킷이름
                    .sourceKey(sourceKey)//원본의 키->temp/폴더의 키
                    .destinationBucket(BUCKET_NAME)//복사할 버킷이름
                    .destinationKey(destinationKey)//upload/폴더의 키
                    .acl(ObjectCannedACL.PUBLIC_READ)//로그인하지 않아도 이미지를 볼 수 있도록
                    .build();

            CopyObjectResponse result = s3Client.copyObject(copyObjectRequest);

            return S3Response.builder()
                    .url(s3Client.utilities()
                            .getUrl(builder -> builder.bucket(BUCKET_NAME).key(destinationKey).build()).toString())
                    .bucketKey(destinationKey)
                    .build();

        } catch (S3Exception e) {
            throw new RuntimeException("S3 파일 이동 실패", e);
        } catch (Exception e) {
            throw new RuntimeException("파일 이동 중 오류 발생", e);
        }
    }

    /**
     * 게시물의 이미지를 모두 삭제합니다.
     *
     * @param boardId 게시글 id
     * @throws RuntimeException 파일 삭제 실패 시 발생
     */
    public void deleteBoardImage(Long boardId) {
        try {
            // 게시글의 이미지 목록
            String imageUrlPath = UPLOAD_PATH + boardId + "/";

            s3Client.listObjectsV2Paginator(ListObjectsV2Request.builder()
                            .bucket(BUCKET_NAME).prefix(imageUrlPath)
                            .build()).stream()
                    .flatMap(response->response.contents().stream())
                    .forEach(s3Object->{
                        s3Client.deleteObject(builder->builder.bucket(BUCKET_NAME).key(s3Object.key()).build());
                    });
        } catch (Exception e) {
            throw new RuntimeException("임시 파일 삭제 실패", e);
        }
    }

    // 프로필 이미지 파일 키 생성
    private String getProfileKey(Long employeeId) {
        return "profile/profile" + employeeId;
    }

    // 프로필 이미지 삭제 없어도 가능하게끔 수정
    public void deleteProfileImage(Long employeeId) {
        String key = getProfileKey(employeeId);
        try {
            s3Client.deleteObject(DeleteObjectRequest.builder()
                    .bucket(BUCKET_NAME)
                    .key(key)
                    .build());
        } catch (S3Exception e) {
            if (e.statusCode() == 404) {
                // 파일이 없으면 무시
                System.out.println("프로필 이미지가 없습니다: " + key);
            } else {
                throw new RuntimeException("프로필 이미지 삭제 중 오류 발생", e);
            }
        }
    }
}
