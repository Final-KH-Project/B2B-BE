package kh.gangnam.b2b.repository;

import jakarta.transaction.Transactional;
import kh.gangnam.b2b.entity.auth.Refresh;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshRepository extends JpaRepository<Refresh, Long> {

    Boolean existsByRefresh(String refresh);
    Optional<Refresh> findByUsername(String username);
    Optional<Refresh> findByRefresh(String refresh);

    @Transactional
    void deleteByUsername(String username);

    @Transactional
    void deleteByRefresh(String refresh);
}
