package kh.gangnam.b2b.entity.auth;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import kh.gangnam.b2b.dto.employee.request.UpdateProfileRequest;
import kh.gangnam.b2b.entity.BaseTimeEntity;
import lombok.*;

@Entity
@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Employee extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long employeeId;


    private String loginId;
    private String password;
    private String name;
    private String profile;
    private String department;
    private String position;
    private String dateOfBirth;
    private String phoneNumber;
    private String address;

    private String role;


    public void updateProfile(UpdateProfileRequest request) {
        if (request.getLoginId() != null) {
            this.loginId = request.getLoginId();
        }
        if (request.getPhoneNumber() != null) {
            this.phoneNumber = request.getPhoneNumber();
        }
        if (request.getAddress() != null) {
            this.address = request.getAddress();
        }
    }
}
