package kh.gangnam.b2b.entity.auth;

import jakarta.persistence.*;
import kh.gangnam.b2b.entity.dept.Dept;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "employee_id")
    private Long employeeId;

    @ManyToOne
    @JoinColumn(name = "dept_id")
    private Dept dept;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "manager_id", referencedColumnName = "employee_id")
    private Employee manager;

    @Column(name = "login_id", nullable = false)
    private String loginId;
    @Column(name = "password")
    private String password;
    @Column(name = "name", nullable = false)
    private String name;
    @Column(name = "salary")
    private BigDecimal salary;
    @Column(name = "profile")
    private String profile;
    @Column(name = "date_of_birth", nullable = false)
    private String dateOfBirth;
    @Column(name = "phone_number", nullable = false)
    private String phoneNumber;
    @Column(name = "position")
    private String position;

    @Column(name = "role")
    private String role;
}
