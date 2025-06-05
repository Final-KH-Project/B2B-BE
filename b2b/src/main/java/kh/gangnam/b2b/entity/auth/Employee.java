package kh.gangnam.b2b.entity.auth;

import jakarta.persistence.*;
import kh.gangnam.b2b.dto.employee.request.UpdateProfileRequest;
import kh.gangnam.b2b.entity.BaseTimeEntity;
import kh.gangnam.b2b.entity.Dept;
import lombok.*;
import jakarta.validation.constraints.Pattern;


@Entity
@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Employee extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "employee_id")
    private Long employeeId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dept_id")
    private Dept dept;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "manager_id")
    private Employee manager;

    @Column(name = "login_id", nullable = false)
    private String loginId;
    @Column(name = "password", nullable = false)
    private String password;
    @Column(name = "name", nullable = false)
    private String name;
    @Column(name = "profile")
    private String profile;
    @Column(name = "position")
    private String position;
    @Column(name = "date_of_birth", nullable = false)
    private String dateOfBirth;
    @Column(name = "phone_number", nullable = false)
    @Pattern(regexp = "^\\d{2,3}-\\d{3,4}-\\d{4}$")
    private String phoneNumber;
    @Column(name = "address")
    private String address;
    @Column(name = "role")
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
