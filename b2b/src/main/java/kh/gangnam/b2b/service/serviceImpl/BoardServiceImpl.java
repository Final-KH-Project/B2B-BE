package kh.gangnam.b2b.service.serviceImpl;

import kh.gangnam.b2b.dto.board.BoardDTO;
import kh.gangnam.b2b.dto.board.request.SaveBoard;
import kh.gangnam.b2b.dto.board.request.UpdateBoard;
import kh.gangnam.b2b.service.BoardService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BoardServiceImpl implements BoardService {

    // Board 서비스 비즈니스 로직 구현

    @Override
    public ResponseEntity<BoardDTO> saveBoard(SaveBoard saveBoard) {
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

}
