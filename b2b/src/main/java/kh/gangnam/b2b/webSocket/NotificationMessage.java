package kh.gangnam.b2b.webSocket;

import lombok.Data;
import lombok.Getter;

@Data
public class NotificationMessage {

    private String name;
    private String title;
    private String content;
}
