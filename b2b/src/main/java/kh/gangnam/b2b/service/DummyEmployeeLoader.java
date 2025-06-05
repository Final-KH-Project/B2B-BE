package kh.gangnam.b2b.service;

import kh.gangnam.b2b.dto.auth.request.JoinRequest;
import kh.gangnam.b2b.dto.dept.DeptCreateRequest;
import kh.gangnam.b2b.dto.dept.DeptDTO;
import kh.gangnam.b2b.dto.employee.request.PositionUpdateRequest;
import kh.gangnam.b2b.service.ServiceImpl.AuthServiceImpl;
import kh.gangnam.b2b.service.ServiceImpl.DeptServiceImpl;
import kh.gangnam.b2b.service.ServiceImpl.EmployeeServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DummyEmployeeLoader implements CommandLineRunner {

    private final AuthServiceImpl authService;
    private final DeptServiceImpl deptService;
    private final EmployeeServiceImpl employeeService; // 직급 변경 서비스 추가

    @Override
    public void run(String... args) {
        // 1. 100명 사용자 생성
        createDummyUsers();

        // 2. 모든 부서 생성
        DeptDTO ceo = deptService.createDept(new DeptCreateRequest("CEO", "본사", null, null));
        DeptDTO executive = deptService.createDept(new DeptCreateRequest("경영진", "본사", ceo.getDeptId(), null));
        DeptDTO devManager = deptService.createDept(new DeptCreateRequest("개발매니져팀", "개발본부", executive.getDeptId(), null));
        DeptDTO designManager = deptService.createDept(new DeptCreateRequest("디자인매니져팀", "디자인본부", executive.getDeptId(), null));
        DeptDTO customer = deptService.createDept(new DeptCreateRequest("고객대응팀", "고객센터", executive.getDeptId(), null));
        DeptDTO hr = deptService.createDept(new DeptCreateRequest("인사과", "인사팀", executive.getDeptId(), null));
        DeptDTO dev1 = deptService.createDept(new DeptCreateRequest("개발1팀", "개발본부 3층", devManager.getDeptId(), null));
        DeptDTO dev2 = deptService.createDept(new DeptCreateRequest("개발2팀", "개발본부 4층", devManager.getDeptId(), null));
        DeptDTO design1 = deptService.createDept(new DeptCreateRequest("디자인1팀", "디자인관 201호", designManager.getDeptId(), null));
        DeptDTO design2 = deptService.createDept(new DeptCreateRequest("디자인2팀", "디자인관 202호", designManager.getDeptId(), null));

        // 3. 부서장 지정 (1~10번 사용자)
        assignDeptHeads(
                ceo.getDeptId(),
                executive.getDeptId(),
                devManager.getDeptId(),
                designManager.getDeptId(),
                customer.getDeptId(),
                hr.getDeptId(),
                dev1.getDeptId(),
                dev2.getDeptId(),
                design1.getDeptId(),
                design2.getDeptId()
        );

        // 4. 나머지 사원 배정 (11~100번 사용자)
        assignRemainingEmployees(
                executive.getDeptId(),
                devManager.getDeptId(),
                designManager.getDeptId(),
                customer.getDeptId(),
                hr.getDeptId(),
                dev1.getDeptId(),
                dev2.getDeptId(),
                design1.getDeptId(),
                design2.getDeptId()
        );

        // 5. 직급 배정
        assignPositions();
    }

    // 100명 사용자 생성 메서드
    private void createDummyUsers() {
        for (int i = 1; i <= 100; i++) {
            JoinRequest joinRequest = JoinRequest.builder()
                    .loginId("user" + i)
                    .password("1234")
                    .name("유저" + i)
                    .dateOfBirth("1990-01-" + String.format("%02d", (i % 28) + 1))
                    .address("서울시 강남구 테스트로 " + i + "번지")
                    .phoneNumber("010-1234-" + String.format("%04d", i))
                    .build();

            try {
                authService.join(joinRequest);
            } catch (Exception e) {
                System.out.println("중복 또는 에러 발생: " + joinRequest.getLoginId());
            }
        }
    }

    // 부서장 지정 메서드 (1~10번 사용자)
    private void assignDeptHeads(Long ceoId, Long executiveId, Long devManagerId,
                                 Long designManagerId, Long customerId, Long hrId,
                                 Long dev1Id, Long dev2Id, Long design1Id, Long design2Id) {
        assignHead(ceoId, 1L);
        assignHead(executiveId, 2L);
        assignHead(devManagerId, 3L);
        assignHead(designManagerId, 4L);
        assignHead(customerId, 5L);
        assignHead(hrId, 6L);
        assignHead(dev1Id, 7L);
        assignHead(dev2Id, 8L);
        assignHead(design1Id, 9L);
        assignHead(design2Id, 10L);
    }

    // 나머지 사원 배정 메서드 (11~100번 사용자)
    private void assignRemainingEmployees(Long executiveId, Long devManagerId, Long designManagerId,
                                          Long customerId, Long hrId,
                                          Long dev1Id, Long dev2Id, Long design1Id, Long design2Id) {
        // 경영진: 2명 (11번)
        moveEmployee(11L, executiveId);

        // 개발매니져팀: 3명 (12~13번)
        moveEmployees(12L, 13L, devManagerId);

        // 디자인매니져팀: 4명 (14~16번)
        moveEmployees(14L, 16L, designManagerId);

        // 개발1팀: 15명 (17~31번)
        moveEmployees(17L, 31L, dev1Id);

        // 개발2팀: 15명 (32~46번)
        moveEmployees(32L, 46L, dev2Id);

        // 디자인1팀: 15명 (47~61번)
        moveEmployees(47L, 61L, design1Id);

        // 디자인2팀: 15명 (62~76번)
        moveEmployees(62L, 76L, design2Id);

        // 고객대응팀: 15명 (77~91번)
        moveEmployees(77L, 91L, customerId);

        // 인사과: 15명 (92~100번)
        moveEmployees(92L, 100L, hrId);
    }

    // 단일 사원 이동
    private void moveEmployee(Long employeeId, Long deptId) {
        try {
            deptService.moveEmployeeToDept(employeeId, deptId);
        } catch (Exception e) {
            System.err.println("사원 이동 실패: " + employeeId + " → " + deptId);
        }
    }

    // 범위 내 사원 일괄 이동
    private void moveEmployees(long start, long end, Long deptId) {
        for (long i = start; i <= end; i++) {
            moveEmployee(i, deptId);
        }
    }

    // 부서장 지정 공통 메서드
    private void assignHead(Long deptId, Long employeeId) {
        try {
            deptService.moveEmployeeToDept(employeeId, deptId);
            deptService.assignDeptHead(deptId, employeeId);
        } catch (Exception e) {
            System.err.println("부서장 지정 실패: " + deptId + " - " + employeeId);
        }
    }

    // 직급 배정 메서드
    private void assignPositions() {
        // CEO
        updatePosition(1L, "CEO");

        // EXECUTIVE
        updatePosition(2L, "EXECUTIVE");
        updatePosition(11L, "EXECUTIVE");

        // MANAGER
        updatePosition(3L, "MANAGER");
        updatePosition(12L, "MANAGER");
        updatePosition(13L, "MANAGER");

        // TEAM_LEADER
        updatePosition(4L, "TEAM_LEADER");
        updatePosition(14L, "TEAM_LEADER");
        updatePosition(15L, "TEAM_LEADER");
        updatePosition(16L, "TEAM_LEADER");

        // STAFF (나머지)
        for (long i = 5L; i <= 100L; i++) {
            if (i == 11L || i == 12L || i == 13L || i == 14L || i == 15L || i == 16L) continue;
            updatePosition(i, "STAFF");
        }
    }

    // 직급 변경 공통 메서드
    private void updatePosition(Long employeeId, String position) {
        try {
            PositionUpdateRequest req = new PositionUpdateRequest();
            req.setEmployeeId(employeeId);
            req.setPosition(position);
            employeeService.updatePosition(req);
        } catch (Exception e) {
            System.err.println("직급 변경 실패: " + employeeId + " → " + position);
        }
    }
}
