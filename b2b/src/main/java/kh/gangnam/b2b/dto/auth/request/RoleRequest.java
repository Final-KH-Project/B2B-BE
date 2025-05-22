package kh.gangnam.b2b.dto.auth.request;

import lombok.Getter;

@Getter
public class RoleRequest {
    private Long employeeId;
    private String position;
    private String role;
}
