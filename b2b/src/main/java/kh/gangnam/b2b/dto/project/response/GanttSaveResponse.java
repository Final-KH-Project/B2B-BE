package kh.gangnam.b2b.dto.project.response;

import kh.gangnam.b2b.entity.project.Task;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class GanttSaveResponse {

    private String text;

    public static GanttSaveResponse fromEntity(Task task) {
        return GanttSaveResponse.builder()
                .text(task.getTitle()).build();
    }
}
