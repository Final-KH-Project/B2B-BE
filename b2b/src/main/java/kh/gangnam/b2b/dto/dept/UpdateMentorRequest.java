package kh.gangnam.b2b.dto.dept;

import lombok.Getter;

@Getter
public class UpdateMentorRequest {
    private Long deptId;
    private Long menteesId;
    private Long mentorId;
}
