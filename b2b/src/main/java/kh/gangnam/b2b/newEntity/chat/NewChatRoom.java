package kh.gangnam.b2b.newEntity;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
public class NewChatRoom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long chatRoomId;

    private String title;

    @OneToMany(mappedBy = "chatRoom")
    private List<ChatRoomEmployee> chatRoomEmployees = new ArrayList<>();
}
