package kh.gangnam.b2b.dto.project.response;

import kh.gangnam.b2b.entity.project.Task;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class GanttUpdateResponse {

    private String text;

    public static GanttUpdateResponse fromEntity(Task task) {
        return GanttUpdateResponse.builder()
                .text(task.getTitle()).build();
    }
}
