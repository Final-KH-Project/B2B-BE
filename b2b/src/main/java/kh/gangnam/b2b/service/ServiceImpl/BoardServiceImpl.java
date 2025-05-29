package kh.gangnam.b2b.service.ServiceImpl;

import jakarta.persistence.EntityNotFoundException;
import kh.gangnam.b2b.dto.board.request.*;
import kh.gangnam.b2b.dto.board.response.CommentSaveResponse;
import kh.gangnam.b2b.dto.board.response.CommentUpdateResponse;
import kh.gangnam.b2b.dto.board.response.EditResponse;
import kh.gangnam.b2b.dto.board.response.MessageResponse;
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
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class BoardServiceImpl implements BoardService {

    private final BoardRepository boardRepository;
    private final EmployeeRepository employeeRepository;
    private final CommentRepository commentRepository;
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
    public CommentSaveResponse saveComment(CommentSaveRequest dto, Long employeeId) {

        // id로 해당 employee,board,comment 찾기
        Employee employee = employeeRepository.findByEmployeeId(employeeId);
        Board board = boardRepository.findById(dto.boardId()).orElseThrow();
        Comment parent = (dto.parentId() != null)?commentRepository.findById(dto.parentId()).orElseThrow():null;

        // comment 테이블에 전달된 정보 저장
        Comment comment = dto.toEntity(board,employee,parent);
        commentRepository.save(comment);

        return CommentSaveResponse.fromEntity(comment,employeeId);
    }

    @Override
    public List<CommentSaveResponse> getCommentList(Long boardId, Long employeeId) {

        // 보드에서 댓글 List로 불러오기
        List<Comment> comment = boardRepository.findById(boardId).orElseThrow().getComments();

        // dto로 변환해서 리턴
        return comment.stream().map((commentSave)->{
            return CommentSaveResponse.fromEntity(commentSave,employeeId);
        }).toList();
    }

    @Override
    public MessageResponse commentDeleteBoard(Long commentId) {

        // 해당 댓글 삭제
        commentRepository.deleteById(commentId);

        return MessageResponse.sendMessage("삭제 성공");
    }

    @Override
    @Transactional
    public CommentUpdateResponse updateComment(CommentUpdateRequest dto, Long employeeId) {

        System.out.println(dto);
        // 해당하는 댓글 entity 찾기
        Comment comment = commentRepository.findById(dto.commentId()).orElseThrow();

        comment.setComment(dto.comment());

        return CommentUpdateResponse.fromEntity(comment,employeeId);
    }

    @Override
    public List<BoardResponse> getListBoard(int type, int page) {
        BoardType boardType = BoardType.useTypeNo(type);

        Sort sort=Sort.by(Sort.Direction.DESC, "boardId");
        Pageable pageable= PageRequest.of(page-1,10, sort);

        return boardRepository.findAllByType(boardType,pageable).stream()
                .map(BoardResponse::fromEntity)
                .toList();
    }

    @Override
    public BoardResponse getBoard(Long boardId,Long employeeId) {
        return boardRepository.findById(boardId)
                .map((board)->{
                    return BoardResponse.fromEntity(board,employeeId);
                })
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
    public MessageResponse deleteBoard(Long boardId) {

        // 게시물과 s3에 업로드된 이미지 삭제
        boardRepository.deleteById(boardId);
        s3ServiceUtil.deleteBoardImage(boardId);


        return MessageResponse.sendMessage("삭제 성공");
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
