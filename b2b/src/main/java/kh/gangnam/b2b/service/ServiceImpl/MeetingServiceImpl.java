package kh.gangnam.b2b.service.ServiceImpl;

import jakarta.transaction.Transactional;
import kh.gangnam.b2b.dto.meeting.request.MeetingRoomRequest;
import kh.gangnam.b2b.dto.meeting.request.ReservationRequest;
import kh.gangnam.b2b.dto.meeting.request.ReservationUpdateRequest;
import kh.gangnam.b2b.dto.meeting.response.MeetingRoomResponse;
import kh.gangnam.b2b.dto.meeting.response.ParticipantReservationResponse;
import kh.gangnam.b2b.dto.meeting.response.ReservationResponse;
import kh.gangnam.b2b.entity.Meeting.MeetingReservation;
import kh.gangnam.b2b.entity.Meeting.MeetingRoom;
import kh.gangnam.b2b.entity.auth.Employee;
import kh.gangnam.b2b.exception.ConflictException;
import kh.gangnam.b2b.exception.InvalidRequestException;
import kh.gangnam.b2b.exception.NotFoundException;
import kh.gangnam.b2b.repository.MeetingReservationRepository;
import kh.gangnam.b2b.repository.MeetingRoomRepository;
import kh.gangnam.b2b.service.shared.EmployeeCommonService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.*;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class MeetingServiceImpl {

    private final MeetingReservationRepository meetingReservationRepo;
    private final MeetingRoomRepository meetingRoomRepo;
    private final EmployeeCommonService employeeCommonService;

    // 회의실 예약
    @Transactional
    public Long createReservation(ReservationRequest request) {
        // 1. 필수 값 검증
        Employee organizer = employeeCommonService
                .getEmployeeOrThrow(request.getOrganizerId(), "주최자를 찾을 수 없음 (ID: " + request.getOrganizerId() + ")");
        MeetingRoom room = validMeetingRoom(request.getRoomId());

        // 2. 시간 유효성 검사
        validateTime(request.getStartTime(), request.getEndTime());

        // 3. 중복 예약 확인
        checkTimeConflict(room, request.getStartTime(), request.getEndTime());

        // 4. 참여자 조회
        Set<Employee> participants = employeeCommonService.getParticipants(request.getParticipantIds());

        // 5. 엔티티 생성
        MeetingReservation reservation = request.toEntity(room, organizer, participants);

        return meetingReservationRepo.save(reservation).getReservationId();
    }

    // 회의실 예약 수정
    @Transactional
    public void updateReservation(ReservationUpdateRequest request) {
        MeetingReservation reservation = validMeetingReservation(request.getReservationId());

        // 회의 종료 시간 이후 수정 불가
        if (LocalDateTime.now().isAfter(reservation.getEndTime())) {
            throw new InvalidRequestException("종료된 회의는 수정 불가");
        }
        // 회의 시간 업데이트 전 검증
        if (request.getStartTime() != null || request.getEndTime() != null) {
            validateTimeUpdate(reservation, request);
        }

        // 참여자 업데이트
        if (request.getParticipantIds() != null) {
            Set<Employee> newParticipants = employeeCommonService.getParticipants(request.getParticipantIds());
            reservation.getParticipants().clear();
            reservation.getParticipants().addAll(newParticipants);
        }

        // 주제 업데이트
        if (StringUtils.hasText(request.getTopic())) {
            reservation.setTopic(request.getTopic());
        }
        if (StringUtils.hasText(request.getPurpose())) {
            reservation.setPurpose(request.getPurpose());
        }

        // 내용 업데이트
        if (StringUtils.hasText(request.getContent())) {
            reservation.setContent(request.getContent());
        }

        // 시간 업데이트
        if (request.getStartTime() != null) {
            reservation.setStartTime(request.getStartTime());
        }
        if (request.getEndTime() != null) {
            reservation.setEndTime(request.getEndTime());
        }
        meetingReservationRepo.save(reservation);
    }
    // 주별 예약 조회 로직
    public List<ReservationResponse> getWeeklyReservations(LocalDate baseDate) {
        LocalDate monday = baseDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDateTime weekStart = monday.atStartOfDay();
        LocalDateTime weekEnd = monday.plusDays(7).atStartOfDay().minusNanos(1);

        return meetingReservationRepo.findAllByStartTimeBetween(weekStart, weekEnd)
                .stream()
                .map(ReservationResponse::from)
                .toList();
    }

//    // 날짜 기준 조회
//    LocalDate inputDate = LocalDate.of(2025, 6, 4); // 수요일 입력
//    LocalDate monday = inputDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));

    // 예약 취소
    @Transactional
    public void cancelReservation(Long reservationId) {
        MeetingReservation meetingReservation = validMeetingReservation(reservationId);

        // 시간 처리
        if (LocalDateTime.now().isAfter(meetingReservation.getStartTime())) {
            throw new InvalidRequestException("이미 시작된 회의는 취소 불가");
        }

        meetingReservationRepo.delete(meetingReservation);
    }

    // 회의실 생성
    public MeetingRoomResponse createMeetingRoom(MeetingRoomRequest request) {
        // 이미 존재하는 Room 인지 확인
        validMeetingRoom(request.roomName());
        return MeetingRoomResponse.fromEntity(meetingRoomRepo.save(request.toEntity()));
    }

    // 회의실 리스트 조회
    public List<MeetingRoomResponse> getAllMeetingRooms() {
        return meetingRoomRepo.findAll().stream()
                .map(MeetingRoomResponse::fromEntity)
                .collect(Collectors.toList());
    }
    // 한 사원에 대해 자신이 참여한 회의(한달치)
    public List<ParticipantReservationResponse> getMyMeetingsByMonth(Long participantId, int year, int month) {
        LocalDateTime start = LocalDateTime.of(year, month, 1, 0, 0);
        LocalDateTime end = start.plusMonths(1);

        return meetingReservationRepo.findMeetingsByParticipantAndMonth(participantId, start, end)
                .stream()
                .map(ParticipantReservationResponse::fromEntity)
                .collect(Collectors.toList());
    }

    private MeetingRoom validMeetingRoom(Long meetingRoomId) {
        return meetingRoomRepo.findById(meetingRoomId)
                .orElseThrow(() -> new NotFoundException("회의실을 찾을 수 없음 (ID: " + meetingRoomId + ")"));
    }
    private void validMeetingRoom(String roomName) {
        if (meetingRoomRepo.existsByRoomName(roomName)) {
            throw new ConflictException("같은 이름의 회의실이 이미 존재합니다. (이름: " + roomName + ")");
        }
    }

    private MeetingReservation validMeetingReservation(Long meetingReservationId) {
        return meetingReservationRepo.findById(meetingReservationId)
                .orElseThrow(() -> new NotFoundException("예약 정보 없음 (ID: " + meetingReservationId + ")"));
    }

    // 자신의 예약 시간을 제외한 채 시간 체크
    private void validateTimeUpdate(MeetingReservation reservation, ReservationUpdateRequest request) {
        // 1. 필수 값 체크
        if (request.getStartTime() == null || request.getEndTime() == null) {
            throw new InvalidRequestException("시작/종료 시간은 함께 입력해야 합니다");
        }

        // 2. 기본 시간 유효성
        validateTime(request.getStartTime(), request.getEndTime());

        // 3. 회의 진행 상태 확인
        if (LocalDateTime.now().isAfter(reservation.getStartTime())) {
            throw new InvalidRequestException("이미 시작된 회의는 시간 변경 불가");
        }

        // 4. 시간대 중복 검사 (현재 예약 제외)
        boolean exists = meetingReservationRepo.existsOverlappingReservationsExcludingSelf(
                reservation.getMeetingRoom().getRoomId(),
                request.getStartTime(),
                request.getEndTime(),
                reservation.getReservationId()
        );
        if (exists) {
            throw new ConflictException("변경하려는 시간에 이미 예약이 존재합니다");
        }
    }

    private void validateTime(LocalDateTime start, LocalDateTime end) {
        if (end.isBefore(start)) throw new InvalidRequestException("종료 시간이 시작 시간보다 빠름");
        if (start.isBefore(LocalDateTime.now())) throw new InvalidRequestException("과거 시간 예약 불가");
    }

    // 시간 가능 여부 체크
    private void checkTimeConflict(MeetingRoom room, LocalDateTime newStart, LocalDateTime newEnd) {
        boolean exists = meetingReservationRepo.existsOverlappingReservations(
                room.getRoomId(),
                newStart,
                newEnd
        );
        if (exists) {
            throw new ConflictException("해당 시간대에 이미 예약이 존재합니다");
        }
    }
}
