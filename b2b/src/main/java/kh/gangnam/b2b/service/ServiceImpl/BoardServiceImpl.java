package kh.gangnam.b2b.service.ServiceImpl;

import kh.gangnam.b2b.dto.board.request.*;
import kh.gangnam.b2b.dto.board.response.CommentSaveResponse;
import kh.gangnam.b2b.dto.board.response.CommentUpdateResponse;
import kh.gangnam.b2b.dto.board.response.EditResponse;
import kh.gangnam.b2b.dto.MessageResponse;
import kh.gangnam.b2b.dto.s3.S3Response;
import kh.gangnam.b2b.entity.auth.Employee;
import kh.gangnam.b2b.exception.NotFoundException;
import kh.gangnam.b2b.repository.board.*;
import kh.gangnam.b2b.service.BoardService;
import kh.gangnam.b2b.service.shared.EmployeeCommonService;
import kh.gangnam.b2b.util.S3ServiceUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Pageable;
import kh.gangnam.b2b.entity.board.*;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BoardServiceImpl implements BoardService {

    private final BoardRepository boardRepo;
    private final CommentRepository commentRepo;
    private final BoardImageRepository imageRepo;
    private final S3ServiceUtil s3ServiceUtil;
    private final EmployeeCommonService employeeCommonService;

    // Board 서비스 비즈니스 로직 구현
    @Override
    @Transactional
    public BoardSaveResponse saveBoard(SaveRequest saveRequest, Long employeeId) {

        // 작성 employee 가져오기
        Employee employee = employeeCommonService
                .getEmployeeOrThrow(employeeId, "해당 사원을 찾을 수 없습니다.");

        // DB에 게시물 저장
        Board board = boardRepo.save(saveRequest.toEntity(employee));
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
        Employee employee = employeeCommonService
                .getEmployeeOrThrow(employeeId, "해당 사원을 찾을 수 없습니다.");

        Board board = (dto.boardId() != null)?boardRepo
                .findById(dto.boardId()).orElseThrow(() -> new NotFoundException("게시판을 찾을 수 없습니다")):null;

        Comment parent = (dto.parentId() != null)?commentRepo
                .findById(dto.parentId()).orElseThrow(() -> new NotFoundException("댓글을 찾을 수 없습니다")):null;


        // comment 테이블에 전달된 정보 저장
        Comment comment = dto.toEntity(board,employee,parent);
        commentRepo.save(comment);

        return CommentSaveResponse.fromEntity(comment,employeeId);
    }

    @Override
    public List<CommentSaveResponse> getCommentList(Long boardId, Long employeeId) {

        // 보드에서 댓글 List로 불러오기
        List<Comment> comment = boardRepo
                .findById(boardId).orElseThrow(() -> new NotFoundException("게시판을 찾을 수 없습니다")).getComments();

        // dto로 변환해서 리턴
        return comment.stream().map((commentSave)->{
            return CommentSaveResponse.fromEntity(commentSave,employeeId);
        }).toList();
    }

    @Override
    public MessageResponse commentDeleteBoard(Long commentId) {

        if (!commentRepo.existsById(commentId)) {
            throw new NotFoundException("삭제할 댓글이 존재하지 않습니다");
        }
        // 해당 댓글 삭제
        commentRepo.deleteById(commentId);

        return MessageResponse.sendMessage("삭제 성공");
    }

    @Override
    @Transactional
    public CommentUpdateResponse updateComment(CommentUpdateRequest dto, Long employeeId) {

        // 해당하는 댓글 entity 찾기
        Comment comment = commentRepo.findById(dto.commentId())
                .orElseThrow(()-> new NotFoundException("댓글이 존재하지 않습니다"));

        comment.setComment(dto.comment());

        return CommentUpdateResponse.fromEntity(comment,employeeId);
    }

    @Override
    public List<CommentSaveResponse> getReplyList(Long commentId, Long employeeId) {

        return commentRepo.findById(commentId).orElseThrow(()-> new NotFoundException("댓글이 존재하지 않습니다"))
                .getChildren().stream().map((comment)->CommentSaveResponse.fromEntity(comment,employeeId)).toList();
    }

    @Override
    public Page<BoardResponse> getListBoard(int type, Pageable pageable) {
        BoardType boardType = BoardType.useTypeNo(type);

        return boardRepo.findByType(boardType, pageable)
                .map(BoardResponse::fromEntity);
    }

    @Override
    public BoardResponse getBoard(Long boardId,Long employeeId) {

        return boardRepo.findById(boardId)
                .map((board)->{
                    return BoardResponse.fromEntity(board,employeeId);
                })
                .orElseThrow(()-> new NotFoundException("게시글이 존재하지 않습니다"));
    }

    @Transactional
    @Override
    public BoardResponse updateBoard(Long boardId, UpdateRequest request) {

        // 게시글 정보 조회 후 저장
        Board board = boardRepo.findById(boardId)
                .orElseThrow(() -> new NotFoundException("게시판을 찾을 수 없습니다")).update(request);

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

        if (!boardRepo.existsById(boardId)) {
            throw new NotFoundException("삭제할 게시글이 존재하지 않습니다");
        }

        // 게시물과 s3에 업로드된 이미지 삭제
        boardRepo.deleteById(boardId);
        s3ServiceUtil.deleteBoardImage(boardId);


        return MessageResponse.sendMessage("삭제 성공");
    }

    @Override
    public EditResponse editBoard(Long boardId) {

        Board board = boardRepo.findById(boardId)
                .orElseThrow(() -> new NotFoundException("게시판을 찾을 수 없습니다"));

        return s3ServiceUtil.editBoardUrl(board);
    }

    @Override
    public String saveS3Image(MultipartFile postFile) {

        S3Response s3Response = s3ServiceUtil.uploadToTemp(postFile);
        String imageUrl = s3Response.getUrl();

        return imageUrl;
    }
}
