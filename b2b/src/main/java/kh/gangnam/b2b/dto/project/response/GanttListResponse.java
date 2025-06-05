package kh.gangnam.b2b.dto.project.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import kh.gangnam.b2b.entity.project.Task;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
public class GanttListResponse {

    private Long id;
    private String text;
    private Integer duration;
    @JsonProperty("start_date")
    private LocalDate startDate;
    private String type;
    private Long parent;
    private double progress;

    public static GanttListResponse fromEntity(Task task){
        return GanttListResponse.builder()
                .id(task.getTaskId()).text(task.getTitle())
                .startDate(task.getStartDate()).duration(task.getDuration())
                .type(task.getType()).progress(task.getProgress())
                .parent(task.getParent().getTaskId())
                .build();
    }
}
