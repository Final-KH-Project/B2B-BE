package kh.gangnam.b2b.service.ServiceImpl;

import kh.gangnam.b2b.dto.board.request.SaveBoard;
import kh.gangnam.b2b.dto.board.request.UpdateBoard;
import kh.gangnam.b2b.dto.board.response.ReadBoard;
import kh.gangnam.b2b.dto.s3.S3Response;
import kh.gangnam.b2b.entity.auth.Employee;
import kh.gangnam.b2b.entity.board.BoardImage;
import kh.gangnam.b2b.entity.board.NoticeBoard;
import kh.gangnam.b2b.repository.EmployeeRepository;
import kh.gangnam.b2b.repository.board.ImgBoardPathRepository;
import kh.gangnam.b2b.repository.board.NoticeBoardRepository;
import kh.gangnam.b2b.service.S3TestService;
import kh.gangnam.b2b.util.S3ServiceUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
public class S3TestServiceImpl implements S3TestService {

    // Board 서비스 비즈니스 로직 구현
    private final EmployeeRepository employeeRepository;
    private final NoticeBoardRepository noticeRepo;
    private final ImgBoardPathRepository imageRepo;
    private final S3ServiceUtil s3ServiceUtil;

    @Override
    public ResponseEntity<?> saveBoard(SaveBoard saveBoard, Long employeeId) {

        List<String> imageUrls = saveBoard.getImageUrls(); // 이미지 url
        String content = saveBoard.getContent();

        try {
            // fk 저장을 위한 user id 찾기
            Employee employee = employeeRepository.findById(employeeId)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            // 보더 테이블에 게시글 정보 저장
            NoticeBoard noticeBoard = noticeRepo.save(saveBoard.toEntity(employee));

            // 반복문을 통해 url 리스트 처리
            for (String url : imageUrls) {

                String fileName = s3ServiceUtil.extractFileNameFromUrl(url);

                // 임시저장된 이미지들 upload/postId 폴더로 복사
                S3Response s3Response = s3ServiceUtil.moveFromTempToUpload(url, noticeBoard.getId());

                // 이미지 테이블에 저장하는 로직
                BoardImage boardImage = imageRepo.save(s3Response.toEntity(noticeBoard));

                if (content.contains(fileName)) {
                    content = content.replace(url, s3Response.getUrl());
                }
            }

            noticeBoard.setContent(content);
            noticeRepo.save(noticeBoard);
        } catch ( Exception e) {
            e.printStackTrace(); // 콘솔에 에러 출력
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("실패: " + e.getMessage());
        }
        return ResponseEntity.ok("게시글 작성");
    }

    @Override
    public ResponseEntity<?> updateBoard(UpdateBoard dto) {

        Long postId = dto.getPostId();
        List<String> imageUrls = dto.getImageUrls();
        String content = dto.getContent();

        try {
            // postId로 해당 게시글 Entity 찾기
            NoticeBoard noticeBoard = noticeRepo.findById(postId)
                    .orElseThrow(() -> new IllegalArgumentException("게시물을 찾을 수 없습니다."));

            // 게시물 수정 내용 저장
            noticeBoard.updateTitleAndContent(dto.getTitle(), dto.getContent());

            // upload 폴더 삭제
            s3ServiceUtil.deleteBoardImage(postId);

            for (String url : imageUrls) {

                String fileName = s3ServiceUtil.extractFileNameFromUrl(url);

                // 임시저장된 이미지들 upload/postId 폴더로 복사
                S3Response s3Response = s3ServiceUtil.moveFromTempToUpload(url, noticeBoard.getId());

                // 이미지 테이블에 저장하는 로직
                BoardImage boardImage = imageRepo.save(s3Response.toEntity(noticeBoard));

                if (content.contains(fileName)) {
                    content = content.replace(url, s3Response.getUrl());
                }
            }

            noticeBoard.setContent(content);
            noticeRepo.save(noticeBoard);

        } catch (Exception e) {
            e.printStackTrace(); // 콘솔에 에러 출력
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("실패: " + e.getMessage());
        }


        return ResponseEntity.ok("게시글 수정 성공");
    }

    @Override
    public ResponseEntity<?> readBoard(Long postId) {

        NoticeBoard noticeBoard = noticeRepo.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시물을 찾을 수 없습니다."));

        ReadBoard dto = ReadBoard.fromEntity(noticeBoard);
        List<BoardImage> imgUrl = noticeBoard.getImage();

        String content = dto.getContent();

        for (BoardImage url : imgUrl) {
            String originalUrl = url.getS3Path();
            String fileName = s3ServiceUtil.extractFileNameFromUrl(originalUrl);

            // upload → temp 복사
            S3Response tempUrl = s3ServiceUtil.moveFromUploadToTemp(originalUrl);

            // content 내부 URL 교체
            if (content.contains(fileName)) {
                content = content.replace(originalUrl, tempUrl.getUrl());
            }
        }
        dto.setContent(content);
        return ResponseEntity.ok(dto);
    }

    @Override
    public ResponseEntity<?> deleteBoard(Long postId) {

        try {
            noticeRepo.deleteById(postId);
            s3ServiceUtil.deleteBoardImage(postId);
        }catch (Exception e){
            e.printStackTrace(); // 콘솔에 에러 출력
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("실패: " + e.getMessage());
        }

        return ResponseEntity.ok("삭제성공");
    }

    @Override
    public ResponseEntity<?> saveS3Image(MultipartFile postFile) {

        String imageUrl = null;
        try {
            S3Response s3Response = s3ServiceUtil.uploadToTemp(postFile);
            imageUrl = s3Response.getUrl();
        } catch (Exception e) {
            e.printStackTrace(); // 콘솔에 에러 출력
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("실패: " + e.getMessage());
        }

        return ResponseEntity.ok(imageUrl);
    }
}
