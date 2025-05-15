package kh.gangnam.b2b.repository;

import kh.gangnam.b2b.entity.auth.Employee;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    Boolean existsByLoginId(String loginId);

    Employee findByLoginId(String loginId);

    Employee findByEmployeeId(Long employeeId);
}
