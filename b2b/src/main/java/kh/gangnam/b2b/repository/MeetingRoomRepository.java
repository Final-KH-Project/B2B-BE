package kh.gangnam.b2b.repository;

import kh.gangnam.b2b.entity.Meeting.MeetingRoom;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MeetingRoomRepository extends JpaRepository<MeetingRoom, Long> {
}
