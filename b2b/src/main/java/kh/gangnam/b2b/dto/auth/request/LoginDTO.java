package kh.gangnam.b2b.dto.auth.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginDTO {
    private String loginId;
    private String password;
}
