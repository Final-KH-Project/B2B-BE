package kh.gangnam.b2b.dto.work.request.leave;

import kh.gangnam.b2b.entity.work.WorkType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class WorkApplyRequest {
    //일일별 근태
    private LocalDate workDate;
    private WorkType workType;
    private String note;//optional
}
