package kh.gangnam.b2b.service.ServiceImpl;

import jakarta.transaction.Transactional;
import kh.gangnam.b2b.dto.dept.DeptCreateRequest;
import kh.gangnam.b2b.dto.dept.DeptDTO;
import kh.gangnam.b2b.dto.dept.UpdateMentorRequest;
import kh.gangnam.b2b.dto.employee.EmployeeDTO;
import kh.gangnam.b2b.entity.Dept;
import kh.gangnam.b2b.entity.auth.Employee;
import kh.gangnam.b2b.repository.DeptRepository;
import kh.gangnam.b2b.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DeptServiceImpl {

    private final EmployeeRepository employeeRepository;
    private final DeptRepository deptRepository;


    // 부서 생성
    @Transactional
    public DeptDTO createDept(DeptCreateRequest request) {
        Dept parentDept = null;
        if (request.getParentDeptId() != null) {
            parentDept = validDeptId(request.getParentDeptId());
        }

        Employee head = null;
        if (request.getHeadId() != null) {
            head = validEmployeeId(request.getHeadId());
        }

        Dept dept = request.toEntity(head, parentDept);
        deptRepository.save(dept);
        return DeptDTO.fromEntity(dept);
    }

    // 부서에 있는 사원 리스트 조회
    public List<EmployeeDTO> getEmployeesByDeptId(Long deptId) {
        List<Employee> employees = employeeRepository.findByDeptDeptId(deptId);
        return employees.stream()
                .map(EmployeeDTO::fromEntity)
                .collect(Collectors.toList());
    }

    // 부서 정보 조회
    public DeptDTO getDeptInfo(Long deptId) {
        Dept dept = validDeptId(deptId);
        return DeptDTO.fromEntity(dept);
    }

    // 사원 부서 변경
    @Transactional
    public void moveEmployeeToDept(Long employeeId, Long newDeptId) {
        // 1. 사원 조회
        Employee employee = validEmployeeId(employeeId);

        // 2. 새 부서 조회
        Dept newDept = validDeptId(newDeptId);

        // 3. 현재 부서와 동일한지 확인 (옵션)
        if (employee.getDept() != null && employee.getDept().getDeptId().equals(newDeptId)) {
            throw new RuntimeException("이미 해당 부서에 소속되어 있습니다.");
        }

        // 4. 부서 변경
        employee.setDept(newDept);
        employeeRepository.save(employee);
    }

    // 부서장 지정
    @Transactional
    public DeptDTO assignDeptHead(Long deptId, Long employeeId) {
        Dept dept = validDeptId(deptId);
        Employee head = validEmployeeId(employeeId);

        dept.setHead(head);
        head.setDept(dept);
        employeeRepository.save(head);
        deptRepository.save(dept);

        return DeptDTO.fromEntity(dept);
    }

    // 부서 내 사수 지정
    public void assignEmployeeMentor(UpdateMentorRequest request) {
        Dept dept = validDeptId(request.getDeptId());
        Employee mentee = validEmployeeInDept(request.getMenteesId(), dept.getDeptId());
        Employee mentor = validEmployeeInDept(request.getMentorId(), dept.getDeptId());

        mentee.setManager(mentor);
        employeeRepository.save(mentee);
    }

    private Employee validEmployeeId(Long employeeId) {
        return employeeRepository.findById(employeeId)
                .orElseThrow(() -> new RuntimeException("해당 사원이 없습니다."));
    }

    private Dept validDeptId(Long deptId) {
        return deptRepository.findById(deptId)
                .orElseThrow(() -> new RuntimeException("부서 정보가 없습니다."));
    }
    private Employee validEmployeeInDept(Long employeeId, Long deptId) {
        return employeeRepository.findByEmployeeIdAndDept_DeptId(employeeId, deptId)
                .orElseThrow(() -> new RuntimeException("해당 부서에 속한 직원을 찾을 수 없습니다."));
    }

}
