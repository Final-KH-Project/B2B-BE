package kh.gangnam.b2b.dto.salary.response;

import kh.gangnam.b2b.entity.Salary;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
public class SalaryResponse {

    private String salaryYearMonth;
    private Long baseSalary;
    private Long incentive;
    private Long bonus;
    private LocalDate salaryDate;
    private String memo;

    public static SalaryResponse fromEntity(Salary salary) {
        return SalaryResponse.builder()
                .salaryYearMonth(salary.getSalaryYearMonth())
                .baseSalary(salary.getBaseSalary())
                .incentive(salary.getIncentive())
                .bonus(salary.getBonus())
                .salaryDate(salary.getSalaryDate())
                .memo(salary.getMemo())
                .build();
    }
}
