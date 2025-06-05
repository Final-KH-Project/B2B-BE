package kh.gangnam.b2b.WebSocket;

import kh.gangnam.b2b.dto.board.request.BoardSaveResponse;
import lombok.*;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
//@Data
public class NotificationMessage {
    private String name;
   // private String title;
   // private String content;
}
