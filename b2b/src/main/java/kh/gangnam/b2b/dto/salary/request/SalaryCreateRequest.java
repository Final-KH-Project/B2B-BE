package kh.gangnam.b2b.dto.salary.request;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SalaryCreateRequest {

    @NotNull
    private Long employeeId;

    @Pattern(regexp = "^\\d{4}-(0[1-9]|1[0-2])$")
    private String salaryYearMonth;

    @Positive
    private Long baseSalary;

    @PositiveOrZero
    private Long incentive;

    @PositiveOrZero
    private Long bonus;

    @FutureOrPresent
    private LocalDate salaryDate;

    private String memo;
}
