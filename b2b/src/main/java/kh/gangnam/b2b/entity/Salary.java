package kh.gangnam.b2b.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Pattern;
import kh.gangnam.b2b.entity.auth.Employee;
import lombok.*;

import java.time.LocalDate;

@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Salary {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "salary_id")
    private Long salaryId;

    // 급여 대상 사원
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id")
    private Employee employee;

    // 지급 년월
    @Column(name = "salary_year_month", nullable = false)
    @Pattern(regexp = "^\\d{4}-(0[1-9]|1[0-2])$") // 2025-06 형식
    private String salaryYearMonth;

    @Column(name = "base_salary", nullable = false)
    private Long baseSalary;

    @Column(name = "incentive", nullable = false)
    private Long incentive;

    @Column(name = "bonus", nullable = false)
    private Long bonus;

    @Column(name = "salary_date", nullable = false)
    private LocalDate salaryDate;

    @Column(name = "memo")
    private String memo;
}
