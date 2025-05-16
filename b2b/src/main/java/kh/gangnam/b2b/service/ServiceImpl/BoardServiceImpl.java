package kh.gangnam.b2b.service.ServiceImpl;

import kh.gangnam.b2b.dto.board.BoardDTO;
import kh.gangnam.b2b.dto.board.request.SaveBoard;
import kh.gangnam.b2b.dto.board.request.UpdateBoard;
import kh.gangnam.b2b.dto.s3.S3Response;
import kh.gangnam.b2b.entity.auth.User;
import kh.gangnam.b2b.entity.board.ImgBoardPath;
import kh.gangnam.b2b.entity.board.NoticeBoard;
import kh.gangnam.b2b.repository.UserRepository;
import kh.gangnam.b2b.repository.board.*;
import kh.gangnam.b2b.service.BoardService;
import kh.gangnam.b2b.util.S3ServiceUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BoardServiceImpl implements BoardService {

    // Board 서비스 비즈니스 로직 구현
    private final UserRepository userRepo;
    private final NoticeBoardRepository noticeRepo;
    private final ImgBoardPathRepository imageRepo;
    private final S3ServiceUtil s3ServiceUtil;

    @Override
    public ResponseEntity<BoardDTO> saveBoard(SaveBoard saveBoard, Long userId) {

        String boardType = saveBoard.getBoardType(); // 게시글 작성 위치
        List<String> imageUrls = saveBoard.getImageUrls(); // 이미지 url

        // fk 저장을 위한 user id 찾기
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        NoticeBoard noticeBoard = noticeRepo.save(saveBoard.toEntity(user));

        // 반복문을 통해 url 리스트 처리
        for (String url : imageUrls) {
            // 임시저장된 이미지들 upload/postId 폴더로 복사
            S3Response s3Response = s3ServiceUtil.moveFromTempToUpload(url, noticeBoard.getId());

            // 이미지 테이블에 저장하는 로직
            ImgBoardPath imgBoardPath = imageRepo.save(s3Response.toEntity(noticeBoard));
        }
        return null;
    }

    @Override
    public ResponseEntity<List<BoardDTO>> readBoards(String type) {
        return null;
    }

    @Override
    public ResponseEntity<BoardDTO> readBoard(String type, Long id) {
        return null;
    }

    @Override
    public ResponseEntity<BoardDTO> updateBoard(UpdateBoard updateBoard) {
        return null;
    }

    @Override
    public ResponseEntity<String> deleteBoard(String type, Long id) {


        return null;
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
