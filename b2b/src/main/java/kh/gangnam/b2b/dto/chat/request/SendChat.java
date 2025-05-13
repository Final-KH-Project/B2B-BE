package kh.gangnam.b2b.dto.chat.request;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

/**
 * 채팅 메시지 전송 요청 DTO
 * - REST로 메시지 전송 시 사용
 * - WebSocket에서는 senderId를 서버에서 추출(클라이언트에서 보내지 않을 수 있음)
 */
@Data
public class SendChat {
    private Long roomId;      // 메시지를 보낼 채팅방 ID
    private Long senderId;    // 메시지 보낸 유저 ID (WebSocket에서는 서버에서 추출)
    private String message;   // 메시지 내용
}