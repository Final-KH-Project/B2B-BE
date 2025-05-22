package kh.gangnam.b2b.entity.dept;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;

import jakarta.persistence.*;
import kh.gangnam.b2b.entity.auth.Employee;
import lombok.Getter;
import lombok.Setter;


@Entity
@Getter
@Setter
public class Dept {
    @Id
    @Column(name = "dept_id")
    private Long deptId;

    // 부서장: Employee PK만 참조 (복합키 아님)
    @OneToOne
    @JoinColumn(name = "head_id", referencedColumnName = "employee_id")
    private Employee head; // 부서장은 0명 또는 1명만 가능

    @Column(name = "dept_name")
    private String deptName;

    @Column(name = "location")
    private String location;

    @ManyToOne
    @JoinColumn(name = "parent_dept_id", referencedColumnName = "dept_id")
    private Dept parentDept;
}
