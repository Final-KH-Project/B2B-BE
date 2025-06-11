package kh.gangnam.b2b.dto.project.response;

import kh.gangnam.b2b.entity.project.Document;
import kh.gangnam.b2b.entity.project.Task;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
public class DocumentListResponse {

    private Long taskId;
    private String title;
    private String subTitle;
    private String author;
    private LocalDate startDate;
    private Integer duration;

    public static DocumentListResponse fromEntity(Task task, Document document){
        return DocumentListResponse.builder()
                .taskId(task.getTaskId()).title(document.getTitle())
                .subTitle(document.getSubTitle())
                .author(document.getAuthor().getName())
                .startDate(task.getStartDate())
                .duration(task.getDuration())
                .build();
    }
}
