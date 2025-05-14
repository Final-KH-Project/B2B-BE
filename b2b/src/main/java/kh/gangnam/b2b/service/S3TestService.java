package kh.gangnam.b2b.service;

import kh.gangnam.b2b.dto.board.BoardDTO;
import kh.gangnam.b2b.dto.board.request.SaveBoard;
import kh.gangnam.b2b.dto.board.request.UpdateBoard;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

public interface S3TestService {

    /**
     * 게시글 생성
     * 게시글 생성 요청 DTO
     * @param saveBoard
     * 작성하기 버튼을 클릭하면 해당 작성글 상세 페이지로 넘어가니 BoardDTO 반환
     * @return
     */
    ResponseEntity<?> saveBoard(SaveBoard saveBoard);

    /**
     * s3 이미 업로드 (차후에 다른 서비스 로직으로 옮겨야 할거 같음)
     * 사용자가 게시글 작성할때 업로드한 이미지
     * @param postFile
     * 업로드 성공 여부 반환
     * @return
     */
    ResponseEntity<?> saveS3Image(MultipartFile postFile);

    /**
     * 게시글 삭제
     *
     * @param postId 삭제할 게시글 id
     * @return 삭제 성공 여부 반환
     */
    ResponseEntity<?> deleteBoard(Long postId);

    /**
     * 게시글 수정
     *
     * @param dto 게시글 수정 정보 DTO
     * @return 수정 성공 여부 반환
     */
    ResponseEntity<?> updateBoard(UpdateBoard dto);

    /**
     * 게시글 읽기
     *
     * @return 내용 반환
     */
    ResponseEntity<?> readBoard(Long postId);
}
