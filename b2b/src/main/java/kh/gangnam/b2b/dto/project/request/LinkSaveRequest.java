package kh.gangnam.b2b.dto.project.request;

import kh.gangnam.b2b.entity.auth.Employee;
import kh.gangnam.b2b.entity.project.Link;
import kh.gangnam.b2b.entity.project.Project;

public record LinkSaveRequest(Long linkId,
                              Long projectId,
                              String source,
                              String target,
                              String type) {

    public Link toEntity(Project project){

        return Link.builder()
                .linkId(linkId).project(project)
                .source(source).target(target)
                .type(type)
                .build();
    }

}
