package kh.gangnam.b2b.dto.meeting.request;

import kh.gangnam.b2b.entity.Meeting.MeetingRoom;

public record MeetingRoomRequest(
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