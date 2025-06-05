package kh.gangnam.b2b.dto.project.response;

import kh.gangnam.b2b.entity.project.Document;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class DocumentGetResponse {

    private Long docId;
    private String title;
    private String subTitle;
    private String content;

    public static DocumentGetResponse fromEntity(Document document){
        return DocumentGetResponse.builder().docId(document.getDocId())
                .title(document.getTitle()).subTitle(document.getSubTitle())
                .content(document.getContent()).build();
    }
}
