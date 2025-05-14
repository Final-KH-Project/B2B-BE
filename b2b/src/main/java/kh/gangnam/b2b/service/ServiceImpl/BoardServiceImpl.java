package kh.gangnam.b2b.service.ServiceImpl;

import jakarta.persistence.EntityNotFoundException;
import kh.gangnam.b2b.dto.board.BoardDTO;
import kh.gangnam.b2b.dto.board.request.*;
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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import kh.gangnam.b2b.entity.board.*;

import java.util.List;

@RequiredArgsConstructor
@Service
@RequiredArgsConstructor
public class BoardServiceImpl implements BoardService {

    private final BoardRepository repository;
    private final UserRepository userRepo;
    private final NoticeBoardRepository noticeRepo;
    private final ImgBoardPathRepository imageRepo;
    private final S3ServiceUtil s3ServiceUtil;

    // Board 서비스 비즈니스 로직 구현

    @Override
    public BoardSaveResponse saveBoard(SaveRequest saveRequest) {

        Board result=repository.save(saveRequest.toEntity());
        System.out.println("[][][]:"+result);
        System.out.println("userId:"+result.getAuthor().getUserId());
        System.out.println("username:"+result.getAuthor().getUsername());
        return BoardSaveResponse.fromEntity(result);
        //return BoardResponse.fromEntity(repository.save(saveRequest.toEntity()));
      
    // Board 서비스 비즈니스 로직 구현


    @Override
    public ResponseEntity<BoardDTO> saveBoard(SaveBoard saveBoard, Long userId) {

        String postType = saveBoard.getPostType(); // 게시글 작성 위치
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
    public List<BoardResponse> getList(int type, int page, int size) {
        BoardType boardType = BoardType.useTypeNo(type);
        /*
        List<BoardResponse> result=repository.findAllByType(boardType).stream()
                .map(BoardResponse::fromEntity)
                .toList();

         */

        Sort sort=Sort.by(Sort.Direction.DESC, "boardId");
        Pageable pageable= PageRequest.of(page-1,size, sort);

        return repository.findAllByType(boardType,pageable).stream()
                .map(BoardResponse::fromEntity)
                .toList();
    }



    @Override
    public BoardResponse get(int type, Long boardId) {
        return repository.findById(boardId)
                .map(BoardResponse::fromEntity)
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 게시글입니다."));
    }

    @Transactional
    @Override
    public BoardResponse update(int type, Long boardId, UpdateRequest request) {
        BoardType boardType = BoardType.useTypeNo(type);

        Board board = repository.findById(boardId).orElseThrow()
                .update(request);

        //return BoardResponse.fromEntity(board);
        return BoardResponse.fromEntity(board);

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
