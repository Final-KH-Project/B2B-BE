package kh.gangnam.b2b.service;


import kh.gangnam.b2b.dto.board.BoardDTO;
import kh.gangnam.b2b.dto.board.request.SaveBoard;
import kh.gangnam.b2b.dto.board.request.UpdateBoard;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface BoardService {

    /**
     * 게시글 생성
     * 게시글 생성 요청 DTO
     * @param saveBoard
     * 작성하기 버튼을 클릭하면 해당 작성글 상세 페이지로 넘어가니 BoardDTO 반환
     * @return
     */
    ResponseEntity<BoardDTO> saveBoard(SaveBoard saveBoard);

    /**
     * 게시글 목록 List 조회
     * 어떤 게시글인지 분류하는 값
     * @param type
     * BoardDTO -> 페이징 예정
     * @return
     */
    ResponseEntity<List<BoardDTO>> readBoards(String type);

    /**
     * 게시글 상세 조회
     * 어떤 게시글인지 분류하는 값
     * @param type
     * 해당 분류 게시글 테이블의 id 값
     * @param id
     * BoardDTO
     * @return
     */
    ResponseEntity<BoardDTO> readBoard(String type, Long id);

    /**
     * 게시글 수정
     * 게시글 업데이트 요청 DTO 내부 필드에 type, id, BoardDTO 가 존재해야 함
     * @param updateBoard
     * 수정하기 버튼을 클릭하면 수정된 게시글 상세 페이지로 넘어가니 수정 게시글 데이터를 보내줘야 함
     * @return
     */
    ResponseEntity<BoardDTO> updateBoard(UpdateBoard updateBoard);

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
}
