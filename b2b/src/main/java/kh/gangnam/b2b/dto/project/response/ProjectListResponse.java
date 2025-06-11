package kh.gangnam.b2b.dto.project.response;

import kh.gangnam.b2b.entity.project.Project;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
public class
ProjectListResponse {

    private Long projectId;
    private String title;
    private LocalDate startDate;
    private LocalDate endDate;
    private String dept;
    private String manager;
    private String description;
    private boolean isAuthor;

    public static ProjectListResponse fromEntity(Project project,Long employeeId){
        return ProjectListResponse.builder()
                .projectId(project.getProjectId())
                .title(project.getTitle()).startDate(project.getStartDate())
                .endDate(project.getEndDate()).dept(project.getDept().getDeptName())
                .manager(project.getManager().getName()).description(project.getDescription())
                .isAuthor(project.getAuthor().getEmployeeId() == employeeId).build();
    }

    public static ProjectListResponse fromEntity(Project project){
        return ProjectListResponse.builder()
                .projectId(project.getProjectId())
                .title(project.getTitle()).startDate(project.getStartDate())
                .endDate(project.getEndDate()).dept(project.getDept().getDeptName())
                .manager(project.getManager().getName()).description(project.getDescription())
                .isAuthor(false).build();
    }
}
