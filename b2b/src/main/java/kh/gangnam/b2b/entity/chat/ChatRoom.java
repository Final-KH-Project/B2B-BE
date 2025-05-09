package kh.gangnam.b2b.entity.chat;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Data
public class ChatRoom {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private LocalDateTime createdAt = LocalDateTime.now();

    // (선택) 채팅방에 속한 유저들: 양방향 매핑 필요 시 주석 해제
    // @OneToMany(mappedBy = "room")
    // private List<ChatRoomUser> members;
}
