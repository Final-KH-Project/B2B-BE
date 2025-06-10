package kh.gangnam.b2b.controller;

import kh.gangnam.b2b.dto.dept.*;
import kh.gangnam.b2b.dto.employee.EmployeeDTO;
import kh.gangnam.b2b.service.ServiceImpl.DeptServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/dept")
public class DeptController {

    private final DeptServiceImpl deptService;

    // 부서 내 인원 조회
    @GetMapping("/get/employees/{deptId}")
    public List<EmployeeDTO> getEmployeesByDeptId(@PathVariable("deptId") Long deptId) {
        return deptService.getEmployeesByDeptId(deptId);
    }
    // 부서 정보 조회
    @GetMapping("/get/info/{deptId}")
    public DeptDTO getDeptInfo(@PathVariable("deptId") Long deptId) {
        return deptService.getDeptInfo(deptId);
    }

    // 모든 부서 정보 조회
    @GetMapping("/get/info/depts")
    public ResponseEntity<List<DeptsDTO>> getDeptsInfo() {
        return ResponseEntity.ok(deptService.getDeptsInfo());
    }

    // 부서 변경
    @PostMapping("/move/employee")
    public void moveEmployeeToDept(@RequestBody MoveEmployeeToDeptRequest request) {
        deptService.moveEmployeeToDept(request.getEmployeeId(), request.getDeptId());
    }

    // 부서장 지정
    @PostMapping("/update/head")
    public DeptDTO assignDeptHead(@RequestBody UpdateHeadRequest request) {
        return deptService.assignDeptHead(request.getDeptId(), request.getHeadId());
    }

    // 부서 생성
    @PostMapping("/create")
    public DeptDTO createDept(@RequestBody DeptCreateRequest request) {
        return deptService.createDept(request);
    }

    // 부서 내 사수 지정
    @PostMapping("/update/mentor")
    public void updateMentor(@RequestBody UpdateMentorRequest request) {
        deptService.assignEmployeeMentor(request);
    }
}
