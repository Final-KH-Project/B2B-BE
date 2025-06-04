package kh.gangnam.b2b.dto.meeting.request;

import kh.gangnam.b2b.entity.Meeting.MeetingReservation;
import kh.gangnam.b2b.entity.Meeting.MeetingRoom;
import kh.gangnam.b2b.entity.auth.Employee;
import lombok.Getter;
import lombok.Setter;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReservationRequest {
    @NotNull(message = "회의실 ID는 필수 항목입니다")
    private Long roomId;

    @NotNull(message = "시작 시간은 필수 항목입니다")
    private LocalDateTime startTime;

    @NotNull(message = "종료 시간은 필수 항목입니다")
    private LocalDateTime endTime;

    @NotNull(message = "주제는 필수 항목입니다")
    private String topic;

    @NotNull(message = "목적은 필수 항목입니다")
    private String purpose;

    @NotNull(message = "내용은 필수 항목입니다")
    private String content;

    @NotNull(message = "주최자 ID는 필수 항목입니다")
    private Long organizerId;

    @NotNull(message = "부서 ID는 필수 항목입니다")
    private Long deptId;

    private List<Long> participantIds;

    public MeetingReservation toEntity(
            MeetingRoom meetingRoom,
            Employee organizer,
            Set<Employee> participants) {

        return MeetingReservation.builder()
                .meetingRoom(meetingRoom)
                .startTime(this.startTime)
                .endTime(this.endTime)
                .topic(this.topic)
                .purpose(this.purpose)
                .content(this.content)
                .organizer(organizer)
                .participants(participants)
                .build();
    }
}
