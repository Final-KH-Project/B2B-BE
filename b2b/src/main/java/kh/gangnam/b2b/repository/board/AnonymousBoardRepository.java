package kh.gangnam.b2b.repository.board;

import kh.gangnam.b2b.entity.board.AnonymousBoard;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AnonymousBoardRepository extends JpaRepository<AnonymousBoard, Long> {
}
