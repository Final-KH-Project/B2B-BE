package kh.gangnam.b2b.dto.chat.request;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * 채팅방 생성 요청 DTO
 * - 프론트엔드에서 채팅방 생성 시 서버로 전달
 * - 참여자(userIds), 채팅방 이름(title) 포함
 */
@Data
public class CreateRoom {
    private String title;          // 생성할 채팅방 이름
    private List<Long> userIds;    // 채팅방에 참여할 유저 ID 리스트
}