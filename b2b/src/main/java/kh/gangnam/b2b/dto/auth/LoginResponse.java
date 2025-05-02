package kh.gangnam.b2b.dto.auth;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class LoginResponse {
    private String accessToken;
    private LocalDateTime expiresAt;
}