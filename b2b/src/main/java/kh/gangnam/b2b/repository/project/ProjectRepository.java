package kh.gangnam.b2b.repository.project;

import kh.gangnam.b2b.entity.project.Project;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjectRepository extends JpaRepository<Project,Long> {
}
