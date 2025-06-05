package kh.gangnam.b2b.dto.project.request;

import kh.gangnam.b2b.entity.auth.Employee;
import kh.gangnam.b2b.entity.project.Document;
import kh.gangnam.b2b.entity.project.Task;

public record DocumentUpdateRequest(Long docId,
                                    String title,
                                    String subTitle,
                                    String content) {
}
