package kh.gangnam.b2b.dto.project.request;

import kh.gangnam.b2b.entity.auth.Employee;
import kh.gangnam.b2b.entity.project.Document;
import kh.gangnam.b2b.entity.project.Project;
import kh.gangnam.b2b.entity.project.Task;

import java.time.LocalDate;

public record GanttSaveRequest(Long projectId,
                               Long taskId,
                               String title,
                               Integer duration,
                               LocalDate startDate,
                               double progress,
                               String type,
                               Long parent) {
    public Task toEntity(Project project, Employee employee,Task parent){

        return Task.builder()
                .taskId(taskId).project(project)
                .createdBy(employee)
                .title(title).duration(duration)
                .startDate(startDate).progress(progress)
                .type(type).parent(parent)
                .build();
    }

    public Document toEntity(Task task, Employee employee){

        return Document.builder()
                .task(task).author(employee)
                .title("").subTitle("")
                .content("")
                .build();
    }
}
