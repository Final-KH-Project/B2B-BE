package kh.gangnam.b2b.entity.work;

import jakarta.persistence.*;
import kh.gangnam.b2b.entity.auth.Employee;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WorkHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 사원과 연관관계
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id")
    private Employee employee;

    // 근무 유형: 출근, 퇴근, 휴가, 반차, 출장 등
    @Enumerated(EnumType.STRING)
    @Column(name = "work_type", nullable = false)
    private WorkType workType;

    // 근무 일자 (날짜)
    @Column(name = "work_date", nullable = false)
    private LocalDate workDate;

    // 출근/퇴근 시간 (휴가, 반차, 출장 등은 null 가능)
    @Column(name = "start_time")
    private LocalDateTime startTime;

    @Column(name = "end_time")
    private LocalDateTime endTime;

    // 비고(옵션)
    @Column(name = "note")
    private String note;


    // 기본 생성자, getter/setter 등
}

