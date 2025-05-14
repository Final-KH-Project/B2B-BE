package kh.gangnam.b2b.dto.board.request;

import kh.gangnam.b2b.entity.auth.User;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserResponse {
    private Long authorId;
    private String username;
    private String name;

    public static UserResponse fromEntity(User author){
        return UserResponse.builder()
                .authorId(author.getUserId())
                .username(author.getUsername())
                .name(author.getName())
                .build();
    }
}
