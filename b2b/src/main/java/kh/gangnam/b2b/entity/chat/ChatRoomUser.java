package kh.gangnam.b2b.entity.chat;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class ChatRoomUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    @ManyToOne
    @JoinColumn(name = "chat_room_id")
    private ChatRoom room;
}
