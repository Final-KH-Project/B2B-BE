package kh.gangnam.b2b.repository.board;

import kh.gangnam.b2b.dto.board.request.BoardResponse;
import kh.gangnam.b2b.entity.board.Board;
import kh.gangnam.b2b.entity.board.BoardType;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BoardRepository extends JpaRepository<Board, Long> {
    List<Board> findAllByType(BoardType result);
    List<Board> findAllByType(BoardType result, Pageable pageable);
}
