package kh.gangnam.b2b.dto.employee.request;

import lombok.Getter;

@Getter
public class UpdateProfileRequest {
    private String loginId;
    private String phoneNumber;
    private String address;
}
