package kh.gangnam.b2b.dto.chat.response;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ReadRooms {
    private Long roomId;
    private String title;
    private LocalDateTime createdAt;
}
