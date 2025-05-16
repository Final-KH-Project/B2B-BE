package kh.gangnam.b2b.repository.board;

import kh.gangnam.b2b.entity.board.BoardImage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ImgBoardPathRepository extends JpaRepository<BoardImage, Long> {
}
