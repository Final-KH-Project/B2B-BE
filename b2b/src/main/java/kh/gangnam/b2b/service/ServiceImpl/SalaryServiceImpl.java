package kh.gangnam.b2b.service.ServiceImpl;

import jakarta.transaction.Transactional;
import kh.gangnam.b2b.dto.employee.Position;
import kh.gangnam.b2b.dto.salary.SalaryStatus;
import kh.gangnam.b2b.dto.salary.request.SalaryCreateRequest;
import kh.gangnam.b2b.dto.salary.request.SalaryPayRequest;
import kh.gangnam.b2b.dto.salary.response.SalaryResponse;
import kh.gangnam.b2b.entity.Dept;
import kh.gangnam.b2b.entity.Salary;
import kh.gangnam.b2b.entity.auth.Employee;
import kh.gangnam.b2b.repository.DeptRepository;
import kh.gangnam.b2b.repository.EmployeeRepository;
import kh.gangnam.b2b.repository.SalaryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

@Service
@RequiredArgsConstructor
@Slf4j
public class SalaryServiceImpl {

    private final SalaryRepository salaryRepo;
    private final EmployeeRepository employeeRepo;
    private final DeptRepository deptRepo;

    @Value("${salary.pay-day}")
    private int salaryPayDay;

    // 비즈니스 로직

    // 사원 개인이 최신 10개 급여
    public Page<SalaryResponse> getMySalaryHistory(Long employeeId, Pageable pageable) {
        Employee employee = validEmployee(employeeId);
        Page<Salary> salaries = salaryRepo
                .findByEmployeeAndSalaryStatusOrderBySalaryDateDesc(employee, SalaryStatus.PAID, pageable);
        return salaries.map(SalaryResponse::fromEntity);
    }


    // 연도별 조회
    public Page<SalaryResponse> getMySalaryByYear(String year, Long employeeId, Pageable pageable) {
        Employee employee = validEmployee(employeeId);
        Page<Salary> salaries = salaryRepo
                .findByEmployeeAndSalaryYearMonthStartingWithAndSalaryStatusOrderBySalaryDateDesc(
                        employee, year, SalaryStatus.PAID, pageable
                );
        return salaries.map(SalaryResponse::fromEntity);
    }

    // 급여 생성 | 수정
    public SalaryResponse createOrUpdateSalary(SalaryCreateRequest request) {
        Employee employee = validEmployee(request.getEmployeeId());
        Salary salary = getOrCreateSalary(employee, request.getSalaryYearMonth());

        // 급여가 이미 지급된 경우 수정 불가
        if (salary.getSalaryStatus() == SalaryStatus.PAID) {
            throw new RuntimeException("지급 완료된 급여는 수정할 수 없습니다");
        }
        // 월은 입력값, 급여일은 yml 값으로 자동 설정
        LocalDate salaryDate = LocalDate.parse(request.getSalaryYearMonth() + "-01")
                .withDayOfMonth(salaryPayDay);

        salary.update(request, employee, salaryDate);
        return SalaryResponse.fromEntity(salaryRepo.save(salary));
    }

    // 단일 급여 지급
    public SalaryResponse paySalary(Long salaryId, SalaryPayRequest request) {
        Salary salary = salaryRepo.findById(salaryId)
                .orElseThrow(() -> new RuntimeException("급여 정보 없음"));
        validatePaymentDate(salary.getSalaryDate());
        updateSalaryStatus(salary);
        salary.setMemo(request.getMemo());
        return SalaryResponse.fromEntity(salaryRepo.save(salary));
    }

    // 전체 사원 급여 일괄 지급
    @Transactional
    public void payAllSalaries(String targetMonth) {
        List<Employee> employees = employeeRepo.findAll();
        employees.forEach(emp -> processPaymentForEmployee(emp, targetMonth));
        log.info("전체 사원 급여 지급 완료 - 대상 월: {}", targetMonth);
    }

    // 부서별 사원 급여 일괄 지급
    @Transactional
    public void paySalariesByDept(Long deptId, String targetMonth) {
        Dept dept = getValidDept(deptId);
        List<Employee> employees = employeeRepo.findByDept(dept);
        employees.forEach(emp ->
                processPaymentForEmployee(emp, targetMonth)
        );
    }

    // 공통 지급 처리 로직 (private)
    private void processPaymentForEmployee(
            Employee employee,
            String targetMonth
    ) {
        salaryRepo.findByEmployeeAndSalaryYearMonth(employee, targetMonth)
                .ifPresentOrElse(
                        salary -> {
                            if (salary.getSalaryStatus() == SalaryStatus.PAID) {
                                log.info("이미 지급된 급여 - 직원 ID: {}, 급여 ID: {}", employee.getEmployeeId(), salary.getSalaryId());
                                return;
                            }
                            // 지급일 생성 (targetMonth + salaryPayDay)
                            LocalDate paidDate = LocalDate.parse(targetMonth + "-01")
                                    .withDayOfMonth(salaryPayDay);

                            // 유효성 검사 및 상태 업데이트
                            validatePaymentDate(paidDate);
                            updateSalaryStatus(salary);
                            salaryRepo.save(salary);
                        },
                        () -> log.warn("급여 정보 없음 - 직원 ID: {}, 월: {}", employee.getEmployeeId(), targetMonth));
    }

    // 전체 급여 조회 (월별 + 페이지네이션)
    public Page<SalaryResponse> getAllSalaries(String yearMonth, Pageable pageable) {
        return salaryRepo.findBySalaryYearMonthOrderBySalaryDateDesc(yearMonth, pageable)
                .map(SalaryResponse::fromEntity);
    }

    // 부서별 급여 조회 (월별 + 페이지네이션)
    public Page<SalaryResponse> getSalariesByDept(Long deptId, String yearMonth, Pageable pageable) {
        Dept dept = getValidDept(deptId);
        return salaryRepo.findByDeptAndSalaryYearMonth(dept, yearMonth, pageable)
                .map(SalaryResponse::fromEntity);
    }

    // 공통 유틸 메서드
    /** 사원과 월로 Salary 조회 또는 생성 */
    private Salary getOrCreateSalary(Employee employee, String targetMonth) {
        return salaryRepo.findByEmployeeAndSalaryYearMonth(employee, targetMonth)
                .orElseGet(() -> Salary.builder()
                        .employee(employee)
                        .salaryYearMonth(targetMonth)
                        .salaryStatus(SalaryStatus.SCHEDULED)
                        .build());
    }

    /** 지급일 유효성 검증 */
    private void validatePaymentDate(LocalDate paidDate) {
        if (paidDate.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("미래 날짜는 지급일로 설정할 수 없습니다");
        }
    }

    /** 급여 상태 및 지급일 업데이트 */
    private void updateSalaryStatus(Salary salary) {
        if (salary.getSalaryStatus() == SalaryStatus.PAID) {
            throw new RuntimeException("이미 지급된 급여입니다");
        }
        salary.setSalaryStatus(SalaryStatus.PAID);
    }

    /** 부서 유효성 검증 및 조회 */
    private Dept getValidDept(Long deptId) {
        return deptRepo.findById(deptId)
                .orElseThrow(() -> new RuntimeException("부서 없음"));
    }

    /** 사원 유효성 검증 및 조회 */
    private Employee validEmployee(Long employeeId) {
        return employeeRepo.findById(employeeId)
                .orElseThrow(() -> new RuntimeException("사원을 찾을 수 없음"));
    }

    @Scheduled(cron = "0 0 0 1 * *") // 매월 1일 00:00 실행
    @Transactional
    public void generateMonthlySalaries() {
        String targetMonth = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM"));
        LocalDate defaultPayDate = LocalDate.now().plusMonths(1).withDayOfMonth(salaryPayDay);  // 예: 다음 달 10일

        employeeRepo.findAll().forEach(employee -> {
            // 해당 월 급여가 없으면 생성
            if (!salaryRepo.existsByEmployeeAndSalaryYearMonth(employee, targetMonth)) {
                Salary salary = Salary.builder()
                        .employee(employee)
                        .salaryYearMonth(targetMonth)
                        .baseSalary(employee.getBaseSalary())  // 사원 계약 급여 사용
                        .incentive(0L)
                        .bonus(0L)
                        .salaryDate(defaultPayDate)
                        .salaryStatus(SalaryStatus.SCHEDULED)
                        .build();
                salaryRepo.save(salary);
            }
        });
    }
    // 서버가 꺼져서 스케쥴러가 안 돌아갈 때 수동으로 급여 내역 생성
    @Transactional
    public void generateMissingSalaries(String targetMonth) {
        String month = (targetMonth != null)
                ? targetMonth
                : LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM"));
        LocalDate defaultPayDate = LocalDate.parse(month + "-01")  // 2025-07-01
                .withDayOfMonth(salaryPayDay);

        employeeRepo.findAll().forEach(employee -> {
            if (!salaryRepo.existsByEmployeeAndSalaryYearMonth(employee, month)) {
                Salary salary = Salary.builder()
                        .employee(employee)
                        .salaryYearMonth(month)
                        .baseSalary(employee.getBaseSalary())
                        .incentive(0L)
                        .bonus(0L)
                        .salaryDate(defaultPayDate)
                        .salaryStatus(SalaryStatus.SCHEDULED)
                        .memo(month + " 기본급")
                        .build();
                salaryRepo.save(salary);
            }
        });
    }

    public void assignSalaryBasedOnPosition(Employee employee) {
        // 직급별 기본 급여 (단위: 원)
        Map<Position, Long> baseSalaries = Map.of(
                Position.CEO, 100_000_000L,
                Position.EXECUTIVE, 70_000_000L,
                Position.MANAGER, 50_000_000L,
                Position.TEAM_LEADER, 35_000_000L,
                Position.STAFF, 25_000_000L
        );

        // 기본 급여 조회 (기본값: STAFF)
        Long baseSalary = baseSalaries.getOrDefault(employee.getPosition(), 25_000_000L);

        // -20% ~ +20% 랜덤 변동
        double variation = ThreadLocalRandom.current().nextDouble(0.8, 1.2);
        Long adjustedSalary = (long) (baseSalary * variation);

        // 급여 생성 요청 DTO
        SalaryCreateRequest request = SalaryCreateRequest.builder()
                .employeeId(employee.getEmployeeId())
                .salaryYearMonth(LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM")))
                .incentive(0L)  // 초기값 0
                .bonus(0L)      // 초기값 0
                .build();

        // 급여 생성/수정
        createOrUpdateSalary(request);
    }

    @Transactional
    public void assignSalariesToAllEmployees() {
        List<Employee> employees = employeeRepo.findAll();
        employees.forEach(employee -> {
            try {
                assignSalaryBasedOnPosition(employee);
            } catch (Exception e) {
                log.error("급여 생성 실패: {} {}", employee.getEmployeeId(), e.getMessage());
            }
        });
    }
}
