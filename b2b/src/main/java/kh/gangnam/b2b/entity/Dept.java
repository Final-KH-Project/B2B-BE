package kh.gangnam.b2b.entity;

import jakarta.persistence.*;
import kh.gangnam.b2b.entity.auth.Employee;
import lombok.*;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Dept {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "dept_id")
    private Long deptId;

    // 부서장 : Employee PK 만 참조
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "head_id", referencedColumnName = "employee_id")
    private Employee head;

    @Column(name = "dept_name")
    private String deptName;

    @Column(name = "location")
    private String location;

    @ManyToOne(cascade = CascadeType.ALL) // 순환 참조 문제로 전부 호출
    @JoinColumn(name = "parent_dept_id", referencedColumnName = "dept_id")
    private Dept parentDept;
}
