package kh.gangnam.b2b.service.ServiceImpl;

import jakarta.persistence.EntityNotFoundException;
import kh.gangnam.b2b.dto.board.request.*;
import kh.gangnam.b2b.dto.board.response.EditResponse;
import kh.gangnam.b2b.dto.s3.S3Response;
import kh.gangnam.b2b.entity.auth.Employee;
import kh.gangnam.b2b.repository.board.*;
import kh.gangnam.b2b.repository.EmployeeRepository;
import kh.gangnam.b2b.service.BoardService;
import kh.gangnam.b2b.util.S3ServiceUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
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
    private final BoardImageRepository imageRepo;
    private final S3ServiceUtil s3ServiceUtil;

    // Board 서비스 비즈니스 로직 구현
    @Override
    @Transactional
    public BoardSaveResponse saveBoard(SaveRequest saveRequest, Long employeeId) {

        // 작성 employee 가져오기
        Employee employee = employeeRepository.findByEmployeeId(employeeId);
        // DB에 게시물 저장
        Board board = boardRepository.save(saveRequest.toEntity(employee));
        String content = saveRequest.content();

        // S3 이미지 주소 변경
        for (String url : saveRequest.imageUrls()) {

            // 파일명 추출
            String fileName = s3ServiceUtil.extractFileNameFromUrl(url);

            // 임시저장된 이미지들 temp/ -> upload/boardId 폳더로 복사
            S3Response s3Response = s3ServiceUtil.moveFromTempToUpload(url, board.getBoardId());

            // 이미지 테이블에 url 저장
            imageRepo.save(s3Response.toEntity(board));

            // content의 url 주소를 temp/ -> upload/BoardId 폴더로 변경
            if (content.contains(fileName)) {
                content = content.replace(url, s3Response.getUrl());
            }
        }

        // 수정된 url 정보 저장
        board.setContent(content);

        // 글 저장 후 저장된 게시글 정보를 보낼 필요는 없어보임
        return BoardSaveResponse.fromEntity(board);

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

        // 게시글 정보 조회 후 저장
        Board board = boardRepository.findById(boardId).orElseThrow().update(request);
        String content = request.content();

        s3ServiceUtil.deleteBoardImage(boardId);

        for (String url : request.imageUrls()) {

            // 파일명 추출
            String fileName = s3ServiceUtil.extractFileNameFromUrl(url);

            // 임시저장된 이미지들 temp/ -> upload/boardId 폳더로 복사
            S3Response s3Response = s3ServiceUtil.moveFromTempToUpload(url, board.getBoardId());

            // 이미지 테이블에 url 저장
            imageRepo.save(s3Response.toEntity(board));

            // content의 url 주소를 temp/ -> upload/BoardId 폴더로 변경
            if (content.contains(fileName)) {
                content = content.replace(url, s3Response.getUrl());
            }
        }

        // 수정된 url 정보 저장
        board.setContent(content);

        //return BoardResponse.fromEntity(board);
        return BoardResponse.fromEntity(board);

    }
    @Override
    @Transactional
    public String deleteBoard(Long boardId) {

        // 게시물과 s3에 업로드된 이미지 삭제
        boardRepository.deleteById(boardId);
        s3ServiceUtil.deleteBoardImage(boardId);

        // cascade 한번에 날리려고 board 테이블에 onetomany 연관관계 추가
        return "삭제 성공함!";
    }

    @Override
    public EditResponse editBoard(Long boardId) {

        Board board = boardRepository.findById(boardId).orElseThrow(()
                -> new RuntimeException("해당 게시글을 찾을 수 없습니다."));
        return s3ServiceUtil.editBoardUrl(board);
    }

    @Override
    public String saveS3Image(MultipartFile postFile) {

        S3Response s3Response = s3ServiceUtil.uploadToTemp(postFile);
        String imageUrl = s3Response.getUrl();

        return imageUrl;
    }
}
