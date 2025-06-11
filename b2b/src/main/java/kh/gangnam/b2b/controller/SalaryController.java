package kh.gangnam.b2b.controller;

import kh.gangnam.b2b.config.security.CustomEmployeeDetails;
import kh.gangnam.b2b.dto.salary.response.SalaryResponse;
import kh.gangnam.b2b.service.ServiceImpl.SalaryServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/salary")
public class SalaryController {

    private final SalaryServiceImpl salaryService;

    // 최신 급여 10개 조회
    @GetMapping("/my")
    public ResponseEntity<Page<SalaryResponse>> getMySalaryHistory(
            @AuthenticationPrincipal CustomEmployeeDetails details,
            @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(salaryService.getMySalaryHistory(details.getEmployeeId(), pageable));
    }

    // 연도별 필터링
    @GetMapping("/my/{year}")
    public ResponseEntity<Page<SalaryResponse>> getMySalaryByYear(
            @PathVariable(name = "year") String year,
            @AuthenticationPrincipal CustomEmployeeDetails details,
            @PageableDefault(size = 10) Pageable pageable
    ) {
        return ResponseEntity.ok(salaryService.getMySalaryByYear(year, details.getEmployeeId(), pageable));
    }
}
