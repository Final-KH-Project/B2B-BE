package kh.gangnam.b2b.controller;

import kh.gangnam.b2b.config.security.CustomEmployeeDetails;
import kh.gangnam.b2b.dto.meeting.request.MeetingRoomRequest;
import kh.gangnam.b2b.dto.meeting.request.ReservationRequest;
import kh.gangnam.b2b.dto.meeting.request.ReservationUpdateRequest;
import kh.gangnam.b2b.dto.meeting.response.MeetingRoomResponse;
import kh.gangnam.b2b.dto.meeting.response.ParticipantReservationResponse;
import kh.gangnam.b2b.dto.meeting.response.ReservationResponse;
import kh.gangnam.b2b.entity.Meeting.MeetingReservation;
import kh.gangnam.b2b.service.ServiceImpl.MeetingServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/meeting")
public class MeetingController {

    private final MeetingServiceImpl meetingService;

    // 전체 회의실 예약 조회(날짜별)
    @GetMapping("/get/reservations/{baseDate}")
    public List<ReservationResponse> getReservations(
            @PathVariable(name = "baseDate")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate baseDate) {

        return meetingService.getWeeklyReservations(baseDate);
    }

    // 회의실 예약
    @PostMapping("/reservation")
    public Long createReservation(@RequestBody ReservationRequest request) {
        return meetingService.createReservation(request);
    }

    // 예약 내용 조정
    @PostMapping("/update/reservation")
    public void updateReservation(@RequestBody ReservationUpdateRequest request) {
        meetingService.updateReservation(request);
    }

    // 회의실 예약 취소
    @DeleteMapping("/delete/reservation/{reservationId}")
    public void deleteReservation(@PathVariable(name = "reservationId") Long reservationId) {
        meetingService.cancelReservation(reservationId);
    }

    // 회의실 생성
    @PostMapping("/create/room")
    public MeetingRoomResponse createMeetingRoom(@RequestBody MeetingRoomRequest request) {
        return meetingService.createMeetingRoom(request);
    }

    // 한 사원에 대해 자신이 당월 참여한 회의
    // 참가자별 월별 조회
    @GetMapping("/{year}/{month}")
    public ResponseEntity<List<ParticipantReservationResponse>> getMyMeetings(
            @AuthenticationPrincipal CustomEmployeeDetails details,
            @PathVariable(name = "year") int year,
            @PathVariable(name = "month") int month
    ) {
        return ResponseEntity.ok(
                meetingService.getMyMeetingsByMonth(details.getEmployeeId(), year, month)
        );
    }

    // 회의실 목록 조회
    @GetMapping("/rooms")
    public List<MeetingRoomResponse> getAllMeetingRooms() {
        return meetingService.getAllMeetingRooms();
    }
}
