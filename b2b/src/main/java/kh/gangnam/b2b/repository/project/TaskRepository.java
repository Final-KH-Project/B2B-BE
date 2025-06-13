package kh.gangnam.b2b.repository.project;

import kh.gangnam.b2b.entity.project.Task;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskRepository extends JpaRepository<Task,Long> {
}
