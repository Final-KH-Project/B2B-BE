package kh.gangnam.b2b.repository.board;

import kh.gangnam.b2b.entity.board.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {
}
