package kh.gangnam.b2b.newEntity;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ChatRoomEmployeeId implements Serializable {

    private Long chatRoomId;
    private Long employeeId;


    @Override
    public int hashCode() {
        return Objects.hash(chatRoomId, employeeId);
    }

    @Override
    public boolean equals(Object obj) {
        if(this == obj) return true;
        if(!(obj instanceof ChatRoomEmployeeId)) return false;
        ChatRoomEmployeeId that = (ChatRoomEmployeeId) obj;
        return Objects.equals(chatRoomId, that.chatRoomId) &&
                Objects.equals(employeeId, that.employeeId);
    }
}
