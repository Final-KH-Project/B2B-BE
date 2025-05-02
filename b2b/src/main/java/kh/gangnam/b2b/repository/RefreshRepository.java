package kh.gangnam.b2b.repository;

import jakarta.transaction.Transactional;
import kh.gangnam.b2b.entity.RefreshEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RefreshRepository extends JpaRepository<RefreshEntity, Long> {

    Boolean existsByRefresh(String refresh);

    @Transactional
    void deleteByUsername(String username);
}
