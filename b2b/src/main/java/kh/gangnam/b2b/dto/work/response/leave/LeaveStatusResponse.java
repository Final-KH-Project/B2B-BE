package kh.gangnam.b2b.dto.work.response.leave;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class LeaveStatusResponse {
    private double totalLeave;
    private double usedLeave;
    private double remainingLeave;
    private double remainingPercentage;

}
