package kh.gangnam.b2b.dto.salary.response;

import kh.gangnam.b2b.entity.Dept;
import kh.gangnam.b2b.entity.Salary;
import kh.gangnam.b2b.entity.auth.Employee;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
public class SalaryResponse {

    // 급여 정보
    private String salaryYearMonth;
    private Long baseSalary;
    private Long incentive;
    private Long bonus;
    private LocalDate salaryDate;
    private String memo;

    // 사원 정보
    private Long employeeId;
    private String employeeName;
    private String position;
    private String deptName;

    public static SalaryResponse fromEntity(Salary salary) {

        Employee employee = salary.getEmployee();
        Dept dept = employee.getDept();

        return SalaryResponse.builder()
                .salaryYearMonth(salary.getSalaryYearMonth())
                .baseSalary(salary.getBaseSalary())
                .incentive(salary.getIncentive())
                .bonus(salary.getBonus())
                .salaryDate(salary.getSalaryDate())
                .memo(salary.getMemo())

                .employeeId(employee.getEmployeeId())
                .employeeName(employee.getName())
                .position(employee.getPosition())
                .deptName(dept != null ? dept.getDeptName() : "미배정")
                .build();
    }
}
