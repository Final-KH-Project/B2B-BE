package kh.gangnam.b2b.repository.board;

import kh.gangnam.b2b.entity.board.EventBoard;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventBoardRepository extends JpaRepository<EventBoard, Long> {
}
