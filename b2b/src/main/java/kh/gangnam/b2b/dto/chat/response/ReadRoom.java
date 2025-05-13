package kh.gangnam.b2b.dto.chat.response;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;


/**
 * 채팅방 상세 조회 응답 DTO
 * - 채팅방 정보와 해당 방의 메시지 리스트 포함
 * - 채팅방 입장/상세 조회 시 프론트로 내려줌
 */
@Data
public class ReadRoom {
    private Long roomId;                     // 채팅방 ID
    private String title;                    // 채팅방 이름
    private LocalDateTime createdAt;         // 채팅방 생성 시각
    private List<ChatMessages> messages;     // 해당 방의 메시지 리스트 (응답 DTO)
}
