package kh.gangnam.b2b.entity.chat;

import jakarta.persistence.*;
import kh.gangnam.b2b.entity.auth.Employee;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

/**
 * 채팅 메시지 엔티티
 * - 반드시 채팅방에 소속됨 (N:1)
 * - 반드시 발신자(User)가 존재함 (N:1)
 * - 단독으로는 조회 불가, 반드시 채팅방 ID로 조회해야 함.
 */
@Entity
@Getter @Setter
public class ChatMessage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 메시지가 속한 채팅방
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chat_room_id")
    private ChatRoom chatRoom;

    // 메시지 보낸 사람
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id")
    private Employee sender;

    private String content;              // 메시지 내용
    private LocalDateTime sentAt = LocalDateTime.now(); // 전송 시각
}
