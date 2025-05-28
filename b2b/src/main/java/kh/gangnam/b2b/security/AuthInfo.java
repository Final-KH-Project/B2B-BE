package kh.gangnam.b2b.security;

import lombok.Builder;
import lombok.Getter;
import org.springframework.security.core.userdetails.UserDetails;

@Getter
@Builder
public class AuthInfo {
    private Long employeeId;
    private String loginId;
    private String role;

    public static AuthInfo from(UserDetails userDetails) {
        return AuthInfo.builder()
                .employeeId(((CustomEmployeeDetails)userDetails).getEmployeeId())
                .loginId(userDetails.getUsername())
                .role(userDetails.getAuthorities().iterator().next().getAuthority())
                .build();
    }
}

