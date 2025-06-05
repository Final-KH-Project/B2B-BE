package kh.gangnam.b2b.service.ServiceImpl;

import kh.gangnam.b2b.dto.salary.request.SalaryCreateRequest;
import kh.gangnam.b2b.dto.salary.response.SalaryResponse;
import kh.gangnam.b2b.entity.Dept;
import kh.gangnam.b2b.entity.Salary;
import kh.gangnam.b2b.entity.auth.Employee;
import kh.gangnam.b2b.repository.DeptRepository;
import kh.gangnam.b2b.repository.EmployeeRepository;
import kh.gangnam.b2b.repository.SalaryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SalaryServiceImpl {

    private final SalaryRepository salaryRepo;
    private final EmployeeRepository employeeRepo;
    private final DeptRepository deptRepo;


    // 최신 10개 급여
    public List<SalaryResponse> getMySalaryHistory(Long employeeId) {
        Employee employee = validEmployee(employeeId);

        List<Salary> salaries = salaryRepo.findTop10ByEmployeeOrderBySalaryDateDesc(employee);
        return salaries.stream()
                .map(SalaryResponse::fromEntity).toList();
    }

    // 연도별 조회
    public List<SalaryResponse> getMySalaryByYear(String year, Long employeeId) {
        Employee employee = validEmployee(employeeId);

        List<Salary> salaries = salaryRepo.findByEmployeeAndSalaryYearMonthStartingWithOrderBySalaryDateDesc(employee, year);
        return salaries.stream()
                .map(SalaryResponse::fromEntity).toList();
    }


    // 급여 생성 | 수정
    public SalaryResponse createOrUpdateSalary(SalaryCreateRequest request) {
        Employee employee = validEmployee(request.getEmployeeId());

        Salary salary = salaryRepo.findByEmployeeAndSalaryYearMonth(employee, request.getSalaryYearMonth())
                .orElseGet(Salary::new);

        salary.update(request, employee);

        return SalaryResponse.fromEntity(salaryRepo.save(salary));
    }
    // 전체 급여 조회
    public Page<SalaryResponse> getAllSalaries(String yearMonth, Pageable pageable) {
        return salaryRepo.findBySalaryYearMonthOrderBySalaryDateDesc(yearMonth, pageable)
                .map(SalaryResponse::fromEntity);
    }

    // 부서별 급여 조회
    public Page<SalaryResponse> getSalariesByDept(Long deptId, String yearMonth, Pageable pageable) {
        Dept dept = deptRepo.findById(deptId)
                .orElseThrow(() -> new RuntimeException("부서 없음"));
        return salaryRepo.findByDeptAndSalaryYearMonth(dept, yearMonth, pageable)
                .map(SalaryResponse::fromEntity);
    }


    // 현재 로그인한 사원 정보 추출
    private Employee validEmployee(Long employeeId) {
        return employeeRepo.findById(employeeId)
                .orElseThrow(() -> new RuntimeException("사원을 찾을 수 없음"));
    }
}
