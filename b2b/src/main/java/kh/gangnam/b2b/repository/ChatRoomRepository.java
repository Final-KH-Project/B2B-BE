package kh.gangnam.b2b.repository;

import kh.gangnam.b2b.entity.chat.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {
}
