package kh.gangnam.b2b.repository.work;

import kh.gangnam.b2b.entity.auth.Employee;
import kh.gangnam.b2b.entity.work.WorkHistory;
import kh.gangnam.b2b.entity.work.WorkType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface WorkHistoryRepository extends JpaRepository<WorkHistory, Long> {

    // 출근 기록 중복 여부 확인(중복 방지 용도, boolean)
    boolean existsByEmployeeAndWorkDateAndWorkType(Employee employee, LocalDate workDate, WorkType workType);

    // 특정 조건으로 근무기록 1건 조회 (추가로 퇴근용 등 확장 가능)
    Optional<WorkHistory> findByEmployeeAndWorkDateAndWorkType(Employee employee, LocalDate workDate, WorkType workType);

    // 한 주간의 기록을 가져오기 위해 사용(근태 현황UI용)
    List<WorkHistory> findAllByEmployeeAndWorkDateBetween(Employee employee, LocalDate startOfWeek, LocalDate endOfWeek);
}
