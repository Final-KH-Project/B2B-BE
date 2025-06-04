package kh.gangnam.b2b.dto.meeting.response;

import kh.gangnam.b2b.entity.auth.Employee;

public record ParticipantInfo(
        Long employeeId,
        String name
    ) {
    static ParticipantInfo fromEntity(Employee employee) {
        return new ParticipantInfo(
                employee.getEmployeeId(),
                employee.getName()
        );
    }
}
