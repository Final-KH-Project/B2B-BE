package kh.gangnam.b2b.repository;

import kh.gangnam.b2b.entity.Dept;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DeptRepository extends JpaRepository<Dept, Long> {
}
