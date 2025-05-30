package kh.gangnam.b2b.WebSocket;

import lombok.*;

/**
 * 메시지 전달용 DTO
 * WebSocket을 통해 프론트로 전송할 데이터를 담는 객체
 * message 필드에 실제 전송할 텍스트나 알림 내용 저장
 */
@NoArgsConstructor
@AllArgsConstructor
//@Data
@Builder
@Getter
public class AlarmMessage {

    private String message;
    private String type;

}
