package kh.gangnam.b2b.service.shared;

import jakarta.transaction.Transactional;
import kh.gangnam.b2b.entity.auth.Employee;
import kh.gangnam.b2b.exception.NotFoundException;
import kh.gangnam.b2b.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class EmployeeCommonService {
    private final EmployeeRepository employeeRepo;

    /**
     * 사원 조회 + validation
     * @param employeeId 사원 id
     * @param msg 에러 메시지
     * @return
     */
    public Employee getEmployeeOrThrow(Long employeeId, String msg) {
        return employeeRepo.findById(employeeId)
                .orElseThrow(() -> new NotFoundException(msg));
    }
    public Employee getEmployeeOrThrow(String loginId, String msg) {
        return employeeRepo.findByLoginId(loginId)
                .orElseThrow(() -> new NotFoundException(msg));
    }
    public Employee getEmployeeInDept(Long employeeId, Long deptId) {
        return employeeRepo.findByEmployeeIdAndDept_DeptId(employeeId, deptId)
                .orElseThrow(() -> new NotFoundException("해당 부서에 속한 직원을 찾을 수 없습니다."));
    }

    public List<Employee> getAll() {
        return employeeRepo.findAll();
    }

    public boolean existsByLoginId(String loginId) {
        return employeeRepo.existsByLoginId(loginId);
    }

    @Transactional
    public Employee saveEmployee(Employee employee) {
        return employeeRepo.save(employee);
    }

    public List<Employee> getEmployeesInDept(long deptId) {
        return employeeRepo.findByDeptDeptId(deptId);
    }

    public Set<Employee> getParticipants(List<Long> participantIds) {
        return new HashSet<>(employeeRepo.findAllById(participantIds));
    }
    public List<Employee> getEmployeesInList(List<Long> employeeIds) {
        return employeeRepo.findAllById(employeeIds);
    }

}
