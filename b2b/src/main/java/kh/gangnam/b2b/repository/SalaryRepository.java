package kh.gangnam.b2b.repository;

import kh.gangnam.b2b.dto.salary.SalaryStatus;
import kh.gangnam.b2b.entity.Dept;
import kh.gangnam.b2b.entity.Salary;
import kh.gangnam.b2b.entity.auth.Employee;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface SalaryRepository extends JpaRepository<Salary, Long> {

    // 사원별, 최신순 10개
    List<Salary> findTop10ByEmployeeAndSalaryStatusOrderBySalaryDateDesc(
            Employee employee, SalaryStatus status
    );

    // 사원별, 연도별
    List<Salary> findByEmployeeAndSalaryYearMonthStartingWithAndSalaryStatusOrderBySalaryDateDesc(
            Employee employee, String year, SalaryStatus status
    );

    Optional<Salary> findByEmployeeAndSalaryYearMonth(Employee employee, String yearMonth);

    // 전체, 부서별, 월별(인사팀용)
    @Query("SELECT s FROM Salary s " +
            "WHERE s.employee.dept = :dept " +
            "AND s.salaryYearMonth = :yearMonth " +
            "ORDER BY s.salaryDate DESC")
    Page<Salary> findByDeptAndSalaryYearMonth(
            @Param("dept") Dept dept,
            @Param("yearMonth") String yearMonth,
            Pageable pageable
    );

    // 전체, 월별(인사팀용)
    Page<Salary> findBySalaryYearMonthOrderBySalaryDateDesc(String yearMonth, Pageable pageable);

    // 급여 존재 여부 확인
    boolean existsByEmployeeAndSalaryYearMonth(Employee employee, String salaryYearMonth);
}
