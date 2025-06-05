package kh.gangnam.b2b.dto.meeting.response;

import kh.gangnam.b2b.dto.employee.Position;
import kh.gangnam.b2b.entity.auth.Employee;

public record OrganizerInfo(
        Long employeeId,
        String name,
        Position position
    ) {
    static OrganizerInfo fromEntity(Employee employee) {
        return new OrganizerInfo(
                employee.getEmployeeId(),
                employee.getName(),
                employee.getPosition()
        );
    }
}
