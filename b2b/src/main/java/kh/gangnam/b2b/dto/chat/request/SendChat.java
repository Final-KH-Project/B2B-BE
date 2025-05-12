package kh.gangnam.b2b.dto.chat.request;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class SendChat {
    private Long roomId;
    private Long senderId;
    private String message;
    // 채팅 전송 요청 DTO
    // ChatMessage 엔티티 필드가 존재해야 함
}
