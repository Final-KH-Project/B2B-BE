package kh.gangnam.b2b.dto.login;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class LoginResponse {
    private String accessToken;
    private LocalDateTime expiresAt;
}