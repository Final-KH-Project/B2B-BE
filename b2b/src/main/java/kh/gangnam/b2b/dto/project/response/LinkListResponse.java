package kh.gangnam.b2b.dto.project.response;

import kh.gangnam.b2b.entity.project.Link;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class LinkListResponse {

    private Long id;
    private String source;
    private String target;
    private String type;

    public static LinkListResponse fromEntity(Link link){
        return LinkListResponse.builder()
                .id(link.getLinkId()).source(link.getSource())
                .target(link.getTarget()).type(link.getType())
                .build();
    }

}
