package kh.gangnam.b2b.entity.Meeting;

import jakarta.persistence.*;

@Entity
public class MeetingRoom {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long roomId;

    @Column(nullable = false, unique = true)
    private String roomName;

    private int capacity;

    private String locationDetail;
}
