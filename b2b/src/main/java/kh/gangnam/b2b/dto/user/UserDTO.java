package kh.gangnam.b2b.dto.user;

import kh.gangnam.b2b.entity.auth.User;
import lombok.Data;

@Data
public class UserDTO {
    private Long UserId;
    private String username;
    private String name;
    private String profile;
    private String department;
    private String position;
    private String dateOfBirth;
    private String phoneNumber;
    private String role;

    public static UserDTO fromEntity(User user) {
        UserDTO dto = new UserDTO();
        dto.setUserId(user.getUserId());
        dto.setUsername(user.getUsername());
        dto.setName(user.getName());
        dto.setProfile(user.getProfile());
        dto.setDepartment(user.getDepartment());
        dto.setPosition(user.getPosition());
        dto.setDateOfBirth(user.getDateOfBirth());
        dto.setPhoneNumber(user.getPhoneNumber());
        dto.setRole(user.getRole());
        return dto;
    }
}
