package kh.gangnam.b2b.service.ServiceImpl;

import jakarta.persistence.EntityNotFoundException;
import kh.gangnam.b2b.dto.board.BoardDTO;
import kh.gangnam.b2b.dto.board.request.*;
import kh.gangnam.b2b.dto.s3.S3Response;
import kh.gangnam.b2b.entity.board.NoticeBoard;
import kh.gangnam.b2b.entity.auth.Employee;
import kh.gangnam.b2b.entity.board.BoardImage;
import kh.gangnam.b2b.repository.board.*;
import kh.gangnam.b2b.repository.EmployeeRepository;
import kh.gangnam.b2b.service.BoardService;
import kh.gangnam.b2b.util.S3ServiceUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import kh.gangnam.b2b.entity.board.*;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BoardServiceImpl implements BoardService {

    private final BoardRepository boardRepository;
    private final EmployeeRepository employeeRepository;
    private final NoticeBoardRepository noticeRepo;
    private final ImgBoardPathRepository imageRepo;
    private final S3ServiceUtil s3ServiceUtil;

    // Board 서비스 비즈니스 로직 구현
    @Override
    public BoardSaveResponse saveBoard(SaveRequest saveRequest, Long employeeId) {

        // TODO 저장하기 전에 S3 이미지 처리 로직

        // TODO S3 이미지 테이블 연관관계 매핑도 필요

        Employee employee = employeeRepository.findByEmployeeId(employeeId);
        Board result = boardRepository.save(saveRequest.toEntity(employee));

        System.out.println("[][][]:" + result);
        System.out.println("employeeId:" + result.getAuthor().getEmployeeId());
        System.out.println("loginId:" + result.getAuthor().getLoginId());
        return BoardSaveResponse.fromEntity(result);
        //return BoardResponse.fromEntity(repository.save(saveRequest.toEntity()));
    }

    @Override
    public List<BoardResponse> getListBoard(int type, int page, int size) {
        BoardType boardType = BoardType.useTypeNo(type);

        Sort sort=Sort.by(Sort.Direction.DESC, "boardId");
        Pageable pageable= PageRequest.of(page-1,size, sort);

        return boardRepository.findAllByType(boardType,pageable).stream()
                .map(BoardResponse::fromEntity)
                .toList();
    }

    @Override
    public BoardResponse getBoard(Long boardId) {
        return boardRepository.findById(boardId)
                .map(BoardResponse::fromEntity)
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 게시글입니다."));
    }

    @Transactional
    @Override
    public BoardResponse updateBoard(Long boardId, UpdateRequest request) {

        // TODO 업데이트 S3 이미지 로직 처리 있어야 함

        Board board = boardRepository.findById(boardId).orElseThrow()
                .update(request);

        //return BoardResponse.fromEntity(board);
        return BoardResponse.fromEntity(board);

    }
    @Override
    public ResponseEntity<String> deleteBoard(Long boardId) {

        // TODO S3 이미지 삭제 및 데이터베이스 컬럼 삭제

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
    @Override
    public ResponseEntity<BoardDTO> saveBoard(SaveBoard saveBoard, Long employeeId) {

        String postType = saveBoard.getBoardType(); // 게시글 작성 위치
        List<String> imageUrls = saveBoard.getImageUrls(); // 이미지 url

        // fk 저장을 위한 user id 찾기
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        NoticeBoard noticeBoard = noticeRepo.save(saveBoard.toEntity(employee));

        // 반복문을 통해 url 리스트 처리
        for (String url : imageUrls) {
            // 임시저장된 이미지들 upload/postId 폴더로 복사
            S3Response s3Response = s3ServiceUtil.moveFromTempToUpload(url, noticeBoard.getId());

            // 이미지 테이블에 저장하는 로직
            BoardImage boardImage = imageRepo.save(s3Response.toEntity(noticeBoard));
        }
        return null;
    }
}
