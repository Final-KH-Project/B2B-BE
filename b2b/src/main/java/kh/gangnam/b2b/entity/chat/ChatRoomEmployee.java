package kh.gangnam.b2b.entity.chat;

import jakarta.persistence.*;
import kh.gangnam.b2b.entity.auth.Employee;
import lombok.Getter;
import lombok.Setter;

/**
 * 채팅방-유저 중간 테이블
 * - 한 유저가 여러 채팅방에, 한 채팅방에 여러 유저가 참여 가능 (N:M)
 */
@Entity
@Getter @Setter
public class ChatRoomEmployee {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 참여 유저
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id")
    private Employee employee;

    // 참여 채팅방
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chat_room_id")
    private ChatRoom chatRoom;
}
