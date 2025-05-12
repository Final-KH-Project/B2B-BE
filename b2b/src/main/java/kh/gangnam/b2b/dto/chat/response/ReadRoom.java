package kh.gangnam.b2b.dto.chat.response;

import kh.gangnam.b2b.entity.chat.ChatMessage;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter @Setter
public class ReadRoom {
    private Long roomId;
    private String title;
    private LocalDateTime createdAt;
    private List<ChatMessage> messages; // 추후 추가

    // getter/setter
    // 채팅방 접속 읽기 응답 DTO
    // 해당 채팅방 엔티티 필드 + List<채팅내역> 필드가 존재
}
