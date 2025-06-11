package kh.gangnam.b2b.dto.meeting.request;

import kh.gangnam.b2b.entity.Meeting.MeetingRoom;
import lombok.Getter;

public record MeetingRoomRequest(
        @Getter
        String roomName,
        int capacity,
        String locationDetail
    ) {
    public MeetingRoom toEntity() {
        return MeetingRoom.builder()
                .roomName(this.roomName)
                .capacity(this.capacity)
                .locationDetail(this.locationDetail)
                .build();
    }
}