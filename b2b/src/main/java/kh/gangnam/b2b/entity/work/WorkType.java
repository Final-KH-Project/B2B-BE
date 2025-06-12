package kh.gangnam.b2b.entity.work;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum WorkType {
    ATTENDANCE("출근"),   // 출근
    LEAVE("퇴근"),        // 퇴근
    VACATION("연차"),     // 휴가
    AM_HALF_DAY("오전 반차"),  // 오전 반차
    PM_HALF_DAY("오후 반차"),  // 오후 반차
    BUSINESS_TRIP("출장"); // 출장

    private final String krName;

    public String getDisplayName() {
        return switch (this){
            case ATTENDANCE -> "출근";
            case LEAVE -> "퇴근";
            case VACATION -> "연차";
            case AM_HALF_DAY ->  "오전 반차";
            case PM_HALF_DAY ->  "오후 반차";
            case BUSINESS_TRIP -> "출장";
        };
    }
}
