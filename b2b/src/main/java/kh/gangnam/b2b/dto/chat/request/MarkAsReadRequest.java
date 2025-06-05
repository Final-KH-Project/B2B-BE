package kh.gangnam.b2b.dto.chat.request;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class MarkAsReadRequest {
    private Long employeeId;
    private Long lastReadMessageId;
    // getter/setter
}
