package kh.gangnam.b2b.repository;

import jakarta.transaction.Transactional;
import kh.gangnam.b2b.entity.auth.Refresh;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface RefreshRepository extends JpaRepository<Refresh, Long> {

    Boolean existsByRefresh(String refresh);

    @Modifying
    @Transactional
    @Query("DELETE FROM Refresh r WHERE r.employeeId = :employeeId")
    void deleteByEmployeeId(@Param("employeeId") Long employeeId);

    @Transactional
    void deleteByRefresh(String refresh);

    Refresh findByEmployeeId(Long employeeId);
}
