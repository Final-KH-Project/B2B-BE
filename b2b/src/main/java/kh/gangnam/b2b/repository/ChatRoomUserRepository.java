package kh.gangnam.b2b.repository;

import kh.gangnam.b2b.entity.chat.ChatRoomUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatRoomUserRepository extends JpaRepository<ChatRoomUser, Long> {
    List<ChatRoomUser> findByUserIdOrderByIdDesc(Long userID);
}
