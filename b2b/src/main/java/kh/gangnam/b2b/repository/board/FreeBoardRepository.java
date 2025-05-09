package kh.gangnam.b2b.repository.board;

import kh.gangnam.b2b.entity.board.FreeBoard;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FreeBoardRepository extends JpaRepository<FreeBoard, Long> {
}
