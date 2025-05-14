package kh.gangnam.b2b.repository.board;

import kh.gangnam.b2b.entity.board.NoticeBoard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NoticeBoardRepository extends JpaRepository<NoticeBoard, Long> {

}
