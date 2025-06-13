package kh.gangnam.b2b.dto.work.response.leave;

import kh.gangnam.b2b.entity.work.WorkType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

public class WorkResponse {
    private LocalDate workDate;
    private WorkType workType;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String note;
}
