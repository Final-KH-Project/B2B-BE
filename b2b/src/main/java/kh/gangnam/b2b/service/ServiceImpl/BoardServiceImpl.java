package kh.gangnam.b2b.service.ServiceImpl;

import kh.gangnam.b2b.dto.board.BoardDTO;
import kh.gangnam.b2b.dto.board.request.SaveBoard;
import kh.gangnam.b2b.dto.board.request.UpdateBoard;
import kh.gangnam.b2b.repository.board.AnonymousBoardRepository;
import kh.gangnam.b2b.repository.board.EventBoardRepository;
import kh.gangnam.b2b.repository.board.FreeBoardRepository;
import kh.gangnam.b2b.repository.board.NoticeBoardRepository;
import kh.gangnam.b2b.service.BoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BoardServiceImpl implements BoardService {

    // Board 서비스 비즈니스 로직 구현
    private final NoticeBoardRepository noticeRepo;
    private final FreeBoardRepository freeRepo;
    private final EventBoardRepository eventRepo;
    private final AnonymousBoardRepository anonymousRepo;

    @Override
    public ResponseEntity<BoardDTO> saveBoard(SaveBoard saveBoard) {

        String postType = saveBoard.getPostType();
        List<String> imageUrls = saveBoard.getImageUrls();
        switch (postType) {
            case "notice":
                // 자유 게시판에 저장
                break;
            case "free":
                // 공지 게시판에 저장
                break;
            case "event":
                // 이벤트 게시판에 저장
                break;
            case "anonymous":
                // 익명 게시판에 저장
                break;
        }

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
