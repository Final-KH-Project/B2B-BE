package kh.gangnam.b2b.repository;

import kh.gangnam.b2b.entity.Meeting.MeetingReservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface MeetingReservationRepository extends JpaRepository<MeetingReservation, Long> {

    @Query("SELECT CASE WHEN COUNT(r) > 0 THEN true ELSE false END " +
            "FROM MeetingReservation r " +
            "WHERE r.meetingRoom.roomId = :roomId " +
            "AND (:newStart < r.endTime AND :newEnd > r.startTime)")
    boolean existsOverlappingReservations(
            @Param("roomId") Long roomId,
            @Param("newStart") LocalDateTime newStart,
            @Param("newEnd") LocalDateTime newEnd
    );

    List<MeetingReservation> findAllByStartTimeBetween(
            LocalDateTime start,
            LocalDateTime end
    );

    @Query("""
        SELECT CASE WHEN COUNT(r) > 0 THEN true ELSE false END 
        FROM MeetingReservation r 
        WHERE r.meetingRoom.roomId = :roomId 
        AND r.reservationId != :excludeId 
        AND (:newStart < r.endTime AND :newEnd > r.startTime)
        """)
    boolean existsOverlappingReservationsExcludingSelf(
            @Param("roomId") Long roomId,
            @Param("newStart") LocalDateTime newStart,
            @Param("newEnd") LocalDateTime newEnd,
            @Param("excludeId") Long excludeId
    );
}
