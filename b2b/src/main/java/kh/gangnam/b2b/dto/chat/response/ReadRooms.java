package kh.gangnam.b2b.dto.chat.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 내 채팅방 목록 조회 응답 DTO
 * - 채팅방 리스트 화면에서 사용
 */
@Data
@AllArgsConstructor
public class ReadRooms {
    private Long roomId;             // 채팅방 ID
    private String title;            // 채팅방 이름
    private LocalDateTime createdAt; // 채팅방 생성 시각
    private String lastMessage;      // ★ 최신 메시지 내용
    private LocalDateTime updatedAt; // ★ 최신 메시지 시간
}