package kh.gangnam.b2b.controller;

import kh.gangnam.b2b.dto.salary.request.SalaryCreateRequest;
import kh.gangnam.b2b.dto.salary.request.SalaryPayRequest;
import kh.gangnam.b2b.dto.salary.response.SalaryResponse;
import kh.gangnam.b2b.service.ServiceImpl.SalaryServiceImpl;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/hr")
public class HrSalaryController {

    private final SalaryServiceImpl salaryService;

    // 급여 생성 | 수정
    @PostMapping("/salary")
    public ResponseEntity<SalaryResponse> createOrUpdateSalary(@RequestBody SalaryCreateRequest request) {
        return ResponseEntity.ok(salaryService.createOrUpdateSalary(request));
    }

    // 급여 단일 지급
    @PutMapping("/salary/{salaryId}/pay")
    public ResponseEntity<SalaryResponse> paySalary(
            @PathVariable(name = "salaryId") Long salaryId,
            @RequestBody SalaryPayRequest request
    ) {
        return ResponseEntity.ok(salaryService.paySalary(salaryId, request));
    }

    // 전체 사원 급여 예정 지급
    @PostMapping("/salary/pay/all")
    public ResponseEntity<Void> payAllSalaries(@RequestBody SalaryPayRequest request) {
        salaryService.payAllSalaries(
                request.getPaidDate(),
                request.getTargetMonth()
        );
        return ResponseEntity.ok().build();
    }

    // 부서별 사원 급여 일괄 지급
    @PostMapping("/salary/pay/dept/{deptId}")
    public ResponseEntity<Void> paySalariesByDept(
            @PathVariable(name = "deptId") Long deptId,
            @RequestBody SalaryPayRequest request
    ) {
        salaryService.paySalariesByDept(
                deptId,
                request.getPaidDate(),
                request.getTargetMonth()
        );
        return ResponseEntity.ok().build();
    }

    @PostMapping("/salaries/assign")
    public ResponseEntity<Void> assignSalariesToAll() {
        salaryService.assignSalariesToAllEmployees();
        return ResponseEntity.ok().build();
    }

    // 전체 급여 조회 (월별 + 페이지네이션)
    @GetMapping("/salary/date/{date}/rowNum/{rowNum}")
    public ResponseEntity<Page<SalaryResponse>> getAllSalaries(
            @PathVariable(name = "date") String yearMonth,
            @PathVariable(name = "rowNum", required = false) Integer rowNum,
            @PageableDefault(size = 50) Pageable pageable
            ) {
        // rowNum null 인 경우 Pageable 재설정
        int pageSize = (rowNum != null) ? rowNum : pageable.getPageSize();
        Pageable customPageable = PageRequest.of(pageable.getPageNumber(), pageSize, pageable.getSort());
        return ResponseEntity.ok(salaryService.getAllSalaries(yearMonth, customPageable));
    }

    // 부서별 급여 조회
    @GetMapping("/salary/dept/{deptId}/date/{date}")
    public ResponseEntity<Page<SalaryResponse>> getSalariesByDept(
            @PathVariable(name = "deptId") Long deptId,
            @PathVariable(name = "date") String yearMonth,
            @PageableDefault(size = 50) Pageable pageable
            ) {
        return ResponseEntity.ok(salaryService.getSalariesByDept(deptId, yearMonth, pageable));
    }

    // 누락 급여 자동 생성
    @PutMapping("/salary/auto-generate/date/{date}")
    public ResponseEntity<Void> generateMissingSalaries(@PathVariable(name = "date") String targetMonth) {
        salaryService.generateMissingSalaries(targetMonth);
        return ResponseEntity.ok().build();
    }


}
