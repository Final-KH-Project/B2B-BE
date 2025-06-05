package kh.gangnam.b2b.entity.work;

import jakarta.persistence.*;
import kh.gangnam.b2b.entity.auth.Employee;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Setter
@Getter
public class LeaveRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long leaveRequestId;

    // 신청자
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id")
    private Employee employee;

    // 결재자(부서장)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "approver_id")
    private Employee approver;

    // ✅필요
    @Enumerated(EnumType.STRING)
    @Column(name = "work_type", nullable = false)
    private WorkType workType; // VACATION, AM_HALF_DAY, PM_HALF_DAY, BUSINESS_TRIP 등

    // ✅필요
    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    // ✅필요
    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ApprovalStatus status = ApprovalStatus.PENDING;

    // ✅필요
    @Column(name = "reason", length = 500)
    private String reason;

    // ✅ 반려 사유 필드 추가
    @Getter
    @Setter
    @Column(name = "reject_reason", length = 500)
    private String rejectReason;
    // 기본 생성자, getter/setter
}

