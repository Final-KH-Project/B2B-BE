package kh.gangnam.b2b.dto.chat.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 채팅 메시지 응답 DTO
 * - 클라이언트에 메시지 정보를 내려줄 때 사용
 * - 엔티티(ChatMessage)와 분리해서 사용 (보안, 확장성)
 */
@Data
@AllArgsConstructor
public class ChatMessages {
    private Long id;           // 메시지 ID
    private Long roomId;       // 채팅방 ID
    private Long senderId;     // 발신자 ID
    private String content;    // 메시지 내용
    private LocalDateTime sentAt; // 보낸 시각
}


