package kh.gangnam.b2b.service.ServiceImpl;

import jakarta.transaction.Transactional;
import kh.gangnam.b2b.dto.dept.DeptCreateRequest;
import kh.gangnam.b2b.dto.dept.DeptDTO;
import kh.gangnam.b2b.dto.dept.DeptsDTO;
import kh.gangnam.b2b.dto.dept.UpdateMentorRequest;
import kh.gangnam.b2b.dto.employee.EmployeeDTO;
import kh.gangnam.b2b.dto.employee.Position;
import kh.gangnam.b2b.dto.employee.request.PositionUpdateRequest;
import kh.gangnam.b2b.entity.Dept;
import kh.gangnam.b2b.entity.auth.Employee;
import kh.gangnam.b2b.exception.NotFoundException;
import kh.gangnam.b2b.repository.DeptRepository;
import kh.gangnam.b2b.service.shared.EmployeeCommonService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DeptServiceImpl {

    private final DeptRepository deptRepository;
    private final EmployeeServiceImpl employeeService;
    private final EmployeeCommonService employeeCommonService;


    // 부서 생성
    @Transactional
    public DeptDTO createDept(DeptCreateRequest request) {
        Dept parentDept = null;
        if (request.getParentDeptId() != null) {
            parentDept = validDeptId(request.getParentDeptId());
        }

        Employee head = null;
        if (request.getHeadId() != null) {
            head = employeeCommonService.getEmployeeOrThrow(request.getHeadId(), "부서장으로 지정할 사원을 찾지 못했습니다.");
        }

        Dept dept = request.toEntity(head, parentDept);
        deptRepository.save(dept);
        return DeptDTO.fromEntity(dept);
    }

    // 부서에 있는 사원 리스트 조회
    public List<EmployeeDTO> getEmployeesByDeptId(Long deptId) {
        return employeeCommonService.getEmployees(deptId).stream()
                .map(EmployeeDTO::fromEntity)
                .collect(Collectors.toList());
    }

    // 부서 정보 조회
    public DeptDTO getDeptInfo(Long deptId) {
        Dept dept = validDeptId(deptId);
        return DeptDTO.fromEntity(dept);
    }

    // 모든 부서 정보 조회
    public List<DeptsDTO> getDeptsInfo() {
        List<Dept> allDepts = deptRepository.findAll();
        return DeptsDTO.buildDeptsTree(allDepts);
    }

    // 사원 부서 변경
    @Transactional
    public void moveEmployeeToDept(Long employeeId, Long newDeptId) {
        // 1. 사원 조회
        Employee employee = employeeCommonService.getEmployeeOrThrow(employeeId, "해당 사원을 찾을 수 없습니다.");

        // 2. 새 부서 조회
        Dept newDept = validDeptId(newDeptId);

        // 3. 현재 부서와 동일한지 확인 (옵션)
        if (employee.getDept() != null && employee.getDept().getDeptId().equals(newDeptId)) {
            throw new RuntimeException("이미 해당 부서에 소속되어 있습니다.");
        }

        // 4. 부서 변경
        employee.setDept(newDept);
        // 5. 사수 초기화
        employee.setManager(null);

        employeeCommonService.saveEmployee(employee);
    }

    // 부서장 지정
    @Transactional
    public DeptDTO assignDeptHead(Long deptId, Long employeeId) {
        Dept dept = validDeptId(deptId);
        Employee newHead = employeeCommonService.getEmployeeOrThrow(employeeId, "부서장으로 지정할 사원을 찾지 못했습니다.");

        // 기존 부서장 일반 직원으로 직급, ROLE 변경
        Employee prevHead = dept.getHead();
        if (prevHead != null) {
            dept.setHead(null);
            employeeService.updatePosition(new PositionUpdateRequest(
                    prevHead.getEmployeeId(),
                    Position.STAFF.getKrName())
            );
        }

        dept.setHead(newHead);
        newHead.setDept(dept);
        employeeCommonService.saveEmployee(newHead);
        deptRepository.save(dept);

        return DeptDTO.fromEntity(dept);
    }

    // 부서 내 사수 지정
    public void assignEmployeeMentor(UpdateMentorRequest request) {
        Dept dept = validDeptId(request.getDeptId());

        Employee mentee = employeeCommonService.getEmployeeInDept(request.getMenteesId(), dept.getDeptId());
        Employee mentor = employeeCommonService.getEmployeeInDept(request.getMentorId(), dept.getDeptId());

        mentee.setManager(mentor);
        employeeCommonService.saveEmployee(mentee);
    }

    private Dept validDeptId(Long deptId) {
        return deptRepository.findById(deptId)
                .orElseThrow(() -> new NotFoundException("부서 정보가 없습니다."));
    }

}
