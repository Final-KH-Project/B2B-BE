package kh.gangnam.b2b.dto.salary.request;

import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SalaryPayRequest {
    @Pattern(regexp = "^\\d{4}-(0[1-9]|1[0-2])$")
    private String targetMonth;

    private String memo;
}