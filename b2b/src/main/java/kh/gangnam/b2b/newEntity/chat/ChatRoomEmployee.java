package kh.gangnam.b2b.newEntity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class ChatRoomEmployee {

    @EmbeddedId
    private ChatRoomEmployeeId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("chatRoomId")
    @JoinColumn(name = "chat_room_id")
    private NewChatRoom chatRoom;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("employeeId")
    @JoinColumn(name = "employee_id")
    private Employee employee;

    private LocalDateTime joinedAt;
    private LocalDateTime lastReadAt;

}
