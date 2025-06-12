package kh.gangnam.b2b.dto.employee.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PositionUpdateRequest {
    private Long employeeId;
    private String position;
}