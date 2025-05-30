package kh.gangnam.b2b.repository;

import kh.gangnam.b2b.entity.auth.Employee;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    Boolean existsByLoginId(String loginId);

    Optional<Employee> findByLoginId(String loginId);

    Optional<Employee> findByEmployeeId(Long employeeId);

    List<Employee> findByDeptDeptId(Long deptId);
}
