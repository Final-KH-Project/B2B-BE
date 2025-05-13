package kh.gangnam.b2b.entity.chat;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 채팅방 엔티티
 * - 여러 명의 유저가 참여할 수 있고, 여러 개의 메시지를 가질 수 있다.
 * - ChatRoomUser(중간테이블)과 1:N, ChatMessage와 1:N 관계.
 */
@Entity
@Getter @Setter
public class ChatRoom {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private LocalDateTime createdAt = LocalDateTime.now();

    // 이 채팅방에 참여 중인 유저 목록 (중간테이블)
    @OneToMany(mappedBy = "chatRoom", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ChatRoomUser> chatRoomUsers = new ArrayList<>();

    // 이 채팅방에 속한 메시지 목록
    @OneToMany(mappedBy = "chatRoom", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ChatMessage> messages = new ArrayList<>();
}