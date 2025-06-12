package kh.gangnam.b2b.repository;

import kh.gangnam.b2b.entity.Meeting.MeetingRoom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MeetingRoomRepository extends JpaRepository<MeetingRoom, Long> {

    Optional<MeetingRoom> findByRoomName(String roomName);

    boolean existsByRoomName(String roomName); // roomName으로 회의실이 존재하는지 확인
}
