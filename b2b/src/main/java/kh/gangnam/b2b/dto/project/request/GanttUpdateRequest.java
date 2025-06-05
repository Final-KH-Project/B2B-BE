package kh.gangnam.b2b.dto.project.request;

import java.time.LocalDate;

public record GanttUpdateRequest(Long taskId,
                                 String title,
                                 Integer duration,
                                 LocalDate startDate,
                                 double progress) {
}
