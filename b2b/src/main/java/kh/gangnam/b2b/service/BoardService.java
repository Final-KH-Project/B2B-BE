package kh.gangnam.b2b.service;


import kh.gangnam.b2b.dto.board.BoardDTO;
import kh.gangnam.b2b.dto.board.request.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface BoardService {

    /**
     * 게시글 생성
     * 게시글 생성 요청 DTO
     * @param saveBoard
     * 작성하기 버튼을 클릭하면 해당 작성글 상세 페이지로 넘어가니 BoardDTO 반환
     * @return
     */
    //ResponseEntity<BoardDTO> saveBoard(SaveBoard saveBoard);

    BoardSaveResponse saveBoard(SaveRequest saveRequest);
    ResponseEntity<BoardDTO> saveBoard(SaveBoard saveBoard, Long userId);

    /**
     * 게시글 목록 List 조회
     * 어떤 게시글인지 분류하는 값
     * @param type
     * BoardDTO -> 페이징 예정
     * @return
     */
    //ResponseEntity<List<BoardDTO>> readBoards(String type);
    List<BoardResponse> getList(int type, int page, int size);

    /**
     * 게시글 상세 조회
     * 어떤 게시글인지 분류하는 값
     * @param type
     * 해당 분류 게시글 테이블의 id 값
     * @param id
     * BoardDTO
     * @return
     */
    //ResponseEntity<BoardDTO> readBoard(String type, Long id);
    BoardResponse get(int type, Long boardId);

    //ResponseEntity<BoardResponse> get(String type, Long id);

    /**
     * 게시글 수정
     * 게시글 업데이트 요청 DTO 내부 필드에 type, id, BoardDTO 가 존재해야 함
     * @param updateBoard
     * 수정하기 버튼을 클릭하면 수정된 게시글 상세 페이지로 넘어가니 수정 게시글 데이터를 보내줘야 함
     * @return
     */
    //ResponseEntity<BoardDTO> updateBoard(UpdateBoard updateBoard);
    BoardResponse update(int type, Long boardId, UpdateRequest request);
    /**
     * 게시글 삭제
     * 어떤 게시글인지 분류하는 값
     * @param type
     * 해당 분류 게시글 테이블의 id 값
     * @param id
     * 삭제 status 성공 여부 및 문자열 반환
     * @return
     */
    ResponseEntity<String> deleteBoard(String type, Long id);


    /**
     * s3 이미 업로드 (차후에 다른 서비스 로직으로 옮겨야 할거 같음)
     * 사용자가 게시글 작성할때 업로드한 이미지
     * @param postFile
     * 업로드 성공 여부 반환
     * @return
     */
    ResponseEntity<?> saveS3Image(MultipartFile postFile);
}
