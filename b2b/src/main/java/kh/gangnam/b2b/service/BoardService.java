package kh.gangnam.b2b.service;


import kh.gangnam.b2b.dto.board.request.*;
import kh.gangnam.b2b.dto.board.response.CommentSaveResponse;
import kh.gangnam.b2b.dto.board.response.CommentUpdateResponse;
import kh.gangnam.b2b.dto.board.response.CustomPageResponse;
import kh.gangnam.b2b.dto.board.response.EditResponse;
import kh.gangnam.b2b.dto.MessageResponse;
import kh.gangnam.b2b.repository.board.CommentUpdateRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;


import java.util.List;

public interface BoardService {

    /**
     * 게시글 생성
     * 게시글 생성 요청 DTO
     * @param saveRequest
     * 작성하기 버튼을 클릭하면 해당 작성글 상세 페이지로 넘어가니 BoardDTO 반환
     * @return
     */
    //ResponseEntity<BoardDTO> saveBoard(SaveBoard saveBoard);

    BoardSaveResponse saveBoard(SaveRequest saveRequest, Long employeeId);

    /**
     * 게시글 목록 List 조회
     * 어떤 게시글인지 분류하는 값
     * @param type
     * BoardDTO -> 페이징 예정
     * @return
     */
    CustomPageResponse<BoardResponse> getListBoard(int type, Pageable pageable);
    CustomPageResponse<BoardResponse> getListBoard(int type, String keyword, Pageable pageable);

    /**
     * 게시글 상세 조회
     * 어떤 게시글인지 분류하는 값
     * @param boardId
     * BoardDTO
     * @return
     */
    BoardResponse getBoard(Long boardId, Long employeeId);

    /**
     * 게시글 수정
     * 게시글 업데이트 요청 DTO 내부 필드에 type, id, BoardDTO 가 존재해야 함
     * @param request
     * 수정하기 버튼을 클릭하면 수정된 게시글 상세 페이지로 넘어가니 수정 게시글 데이터를 보내줘야 함
     * @return
     */
    BoardResponse updateBoard(Long boardId, UpdateRequest request);
    /**
     * 게시글 삭제
     * 어떤 게시글인지 분류하는 값
     * @param BoardId
     * 삭제 status 성공 여부 및 문자열 반환
     * @return
     */
    MessageResponse deleteBoard(Long BoardId);

    /**
     * 게시글 수정할때 정보 요청
     *
     * @param boardId 게시글 id
     * @return 불러오기 성공 여부 반환
     */
    EditResponse editBoard(Long boardId);

    /**
     * s3 이미 업로드
     * 사용자가 게시글 작성할때 업로드한 이미지
     * @param postFile
     * 업로드 성공 여부 반환
     * @return
     */
    String saveS3Image(MultipartFile postFile);

    /**
     * 댓글 저장
     *
     * @param dto 댓글 정보
     * @return 저장 성공 여부 반환
     */
    CommentSaveResponse saveComment(CommentSaveRequest dto, Long employeeId);

    /**
     * 댓글 불러오기
     *
     * @param boardId 보드 아이디
     * @return 해당 보드의 댓글 반환
     */
    List<CommentSaveResponse> getCommentList(Long boardId, Long employeeId);

    /**
     * 댓글 삭제
     *
     * @param commentId 댓글 아이디
     * @return 해당 보드의 삭제 여부 반환
     */
    MessageResponse commentDeleteBoard(Long commentId);

    /**
     * 댓글 수정
     *
     * @param dto 댓글 정보
     * @return 해당 보드의 수정 여부 반환
     */
    CommentUpdateResponse updateComment(CommentUpdateRequest dto, Long employeeId);

    List<CommentSaveResponse> getReplyList(Long commentId, Long employeeId);
}
