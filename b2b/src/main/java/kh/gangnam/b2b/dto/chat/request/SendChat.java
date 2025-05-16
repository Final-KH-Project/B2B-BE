package kh.gangnam.b2b.dto.chat.request;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * 채팅 메시지 전송 요청 DTO
 * - REST로 메시지 전송 시 사용
 * - WebSocket에서는 senderId를 서버에서 추출(클라이언트에서 보내지 않을 수 있음)
 */
@Data
public class SendChat {
    private Long roomId;                // 기존 방이면 ID, 새 방이면 null
    private Long senderId;              // 메시지 보낸 사람
    private String message;             // 메시지 내용
    private String title;               // (선택) 새 방 생성 시 방 제목
    private List<Long> participantUserIds; // (선택) 새 방 생성 시 참여자 리스트
}