package kh.gangnam.b2b.dto.salary.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SalaryPayRequest {
    @NotNull
    @PastOrPresent // 실제 지급일은 과거/현재만 가능
    private LocalDate paidDate;

    @Pattern(regexp = "^\\d{4}-(0[1-9]|1[0-2])$")
    private String targetMonth;

    private String memo;
}