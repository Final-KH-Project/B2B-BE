package kh.gangnam.b2b.dto.meeting.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MeetingRoomCreateRequest {

    private String roomName;
    private int capacity;
    private String locationDetail;
}
