package kh.gangnam.b2b.dto.meeting.response;


import kh.gangnam.b2b.entity.Meeting.MeetingReservation;
import kh.gangnam.b2b.entity.auth.Employee;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
public class ParticipantReservationResponse {
    private Long reservationId;
    private String topic;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String organizerName;
    private List<String> participants;

    public static ParticipantReservationResponse fromEntity(MeetingReservation entity) {
        return ParticipantReservationResponse.builder()
                .reservationId(entity.getReservationId())
                .topic(entity.getTopic())
                .startTime(entity.getStartTime())
                .endTime(entity.getEndTime())
                .organizerName(entity.getOrganizer().getName())
                .participants(entity.getParticipants().stream()
                        .map(Employee::getName)
                        .collect(Collectors.toList()))
                .build();
    }
}
