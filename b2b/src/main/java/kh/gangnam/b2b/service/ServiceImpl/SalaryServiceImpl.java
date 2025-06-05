package kh.gangnam.b2b.service.ServiceImpl;

import kh.gangnam.b2b.dto.salary.response.SalaryResponse;
import kh.gangnam.b2b.entity.Salary;
import kh.gangnam.b2b.entity.auth.Employee;
import kh.gangnam.b2b.repository.EmployeeRepository;
import kh.gangnam.b2b.repository.SalaryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SalaryServiceImpl {

    private final SalaryRepository salaryRepo;
    private final EmployeeRepository employeeRepo;


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

    // 현재 로그인한 사원 정보 추출
    private Employee validEmployee(Long employeeId) {
        return employeeRepo.findById(employeeId)
                .orElseThrow(() -> new RuntimeException("사원을 찾을 수 없음"));
    }


}
