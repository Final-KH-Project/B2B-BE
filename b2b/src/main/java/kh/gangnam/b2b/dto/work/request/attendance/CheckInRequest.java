package kh.gangnam.b2b.dto.work.request.attendance;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class CheckInRequest {
    //출근DTO
    private LocalDateTime startTime; // nullable, 없으면 서버가 now()사용


}
