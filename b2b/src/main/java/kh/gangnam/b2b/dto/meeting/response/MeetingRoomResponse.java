package kh.gangnam.b2b.dto.meeting.response;

import kh.gangnam.b2b.entity.Meeting.MeetingRoom;

public record MeetingRoomResponse(
        Long roomId,
        String roomName,
        int capacity,
        String locationDetail
    ) {
    public static MeetingRoomResponse fromEntity(MeetingRoom room) {
        return new MeetingRoomResponse(
                room.getRoomId(),
                room.getRoomName(),
                room.getCapacity(),
                room.getLocationDetail()
        );
    }
}