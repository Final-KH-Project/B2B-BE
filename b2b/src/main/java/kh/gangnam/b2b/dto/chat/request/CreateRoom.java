package kh.gangnam.b2b.dto.chat.request;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter @Setter
public class CreateRoom {
    private String title;
    private List<Long> userIds;
}
