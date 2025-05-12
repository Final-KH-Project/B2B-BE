package kh.gangnam.b2b.entity.chat;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter @Setter
@Entity
public class ChatMessage {
    @Id
    @GeneratedValue
    private Long id;
    private Long roomId;
    private Long senderId;
    private String message;
    private LocalDateTime sentAt;
}