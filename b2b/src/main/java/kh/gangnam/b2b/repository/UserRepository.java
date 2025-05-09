package kh.gangnam.b2b.repository;

import kh.gangnam.b2b.entity.auth.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

    Boolean existsByUsername(String username);

    User findByUsername(String username);
}
