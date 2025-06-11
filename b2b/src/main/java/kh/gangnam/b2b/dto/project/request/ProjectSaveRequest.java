package kh.gangnam.b2b.dto.project.request;

import kh.gangnam.b2b.entity.Dept;
import kh.gangnam.b2b.entity.auth.Employee;
import kh.gangnam.b2b.entity.project.Project;

import java.time.LocalDate;
import java.util.List;

public record ProjectSaveRequest(String projectName,
                                 LocalDate startDate,
                                 LocalDate endDate,
                                 Long departmentId,
                                 Long employeeId,
                                 List<Long> members,
                                 String description) {

    public Project toEntity(Dept dept,Employee manager,List<Employee> employeeList,Employee id){
        return Project.builder()
                .title(projectName).startDate(startDate)
                .endDate(endDate).description(description)
                .dept(dept).manager(manager)
                .members(employeeList).author(id)
                .build();
    }
}
