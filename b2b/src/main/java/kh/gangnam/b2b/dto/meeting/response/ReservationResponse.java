package kh.gangnam.b2b.dto.meeting.response;

import kh.gangnam.b2b.entity.Meeting.MeetingReservation;
import lombok.Builder;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Builder
public record ReservationResponse(
        Long reservationId,
        MeetingRoomResponse roomInfo,
        LocalDateTime startTime,
        LocalDateTime endTime,
        String topic,
        String purpose,
        String content,
        OrganizerInfo organizer,
        List<ParticipantInfo> participants,
        String departmentName,
        Long deptId
    ) {
    // 엔티티 → DTO 변환 메서드
    public static ReservationResponse from(MeetingReservation entity) {
        return ReservationResponse.builder()
                .reservationId(entity.getReservationId())
                .roomInfo(MeetingRoomResponse.fromEntity(entity.getMeetingRoom()))
                .startTime(entity.getStartTime())
                .endTime(entity.getEndTime())
                .topic(entity.getTopic())
                .purpose(entity.getPurpose())
                .content(entity.getContent())
                .organizer(OrganizerInfo.fromEntity(entity.getOrganizer()))
                .participants(entity.getParticipants().stream()
                        .map(ParticipantInfo::fromEntity)
                        .collect(Collectors.toList()))
                .departmentName(entity.getOrganizer().getDept() != null ?
                        entity.getOrganizer().getDept().getDeptName() : "부서없음")
                .deptId(entity.getOrganizer().getDept() != null ?
                        entity.getOrganizer().getDept().getDeptId(): null)
                .build();
    }
}