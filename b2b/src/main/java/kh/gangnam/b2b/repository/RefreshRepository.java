package kh.gangnam.b2b.repository;

import jakarta.transaction.Transactional;
import kh.gangnam.b2b.entity.RefreshEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshRepository extends JpaRepository<RefreshEntity, Long> {

    Boolean existsByRefresh(String refresh);
    Optional<RefreshEntity> findByUsername(String username);
    Optional<RefreshEntity> findByRefresh(String refresh);

    @Transactional
    void deleteByUsername(String username);

    @Transactional
    void deleteByRefresh(String refresh);
}
