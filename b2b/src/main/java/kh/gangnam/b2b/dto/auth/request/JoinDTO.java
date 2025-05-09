package kh.gangnam.b2b.dto.auth.request;

import kh.gangnam.b2b.entity.auth.User;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class JoinDTO {

    private String username;
    private String password;
    private String name;
    private String profile;
    private String department;
    private String position;
    private String dateOfBirth;
    private String phoneNumber;


    public User toEntity(String encodedPassword, String role) {
        User user = new User();
        user.setUsername(this.username);
        // 비밀번호는 인코딩된 값 사용
        user.setPassword(encodedPassword);
        user.setName(this.name);
        user.setProfile(this.profile);
        user.setDepartment(this.department);
        user.setPosition(this.position);
        user.setDateOfBirth(this.dateOfBirth);
        user.setPhoneNumber(this.phoneNumber);
        user.setRole(role);
        return user;
    }
}
