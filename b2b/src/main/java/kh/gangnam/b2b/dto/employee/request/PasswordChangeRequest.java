package kh.gangnam.b2b.dto.employee.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PasswordChangeRequest {
    private String prePassword;
    private String newPassword;
}