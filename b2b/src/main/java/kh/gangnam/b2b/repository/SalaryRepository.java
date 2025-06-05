package kh.gangnam.b2b.repository;

import kh.gangnam.b2b.entity.Dept;
import kh.gangnam.b2b.entity.Salary;
import kh.gangnam.b2b.entity.auth.Employee;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SalaryRepository extends JpaRepository<Salary, Long> {

    // 사원별, 최신순 10개
    List<Salary> findTop10ByEmployeeOrderBySalaryDateDesc(Employee employee);

    // 사원별, 연도별
    List<Salary> findByEmployeeAndSalaryYearMonthStartingWithOrderBySalaryDateDesc(Employee employee, String year);

    // 전체, 부서별, 월별(인사팀용)
    @Query("SELECT p FROM Salary p " +
            "WHERE p.employee.dept = :dept " +
            "AND p.salaryYearMonth = :yearMonth ORDER BY p.salaryDate DESC")
    List<Salary> findByDeptAndSalaryYearMonth(@Param("dept") Dept dept,
                                        @Param("yearMonth") String yearMonth,
                                        Pageable pageable);

    // 전체, 월별(인사팀용)
    Page<Salary> findBySalaryYearMonthOrderBySalaryDateDesc(String yearMonth, Pageable pageable);
}
