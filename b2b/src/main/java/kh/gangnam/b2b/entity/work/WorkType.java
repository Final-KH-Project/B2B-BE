package kh.gangnam.b2b.entity.work;

import lombok.Getter;
import lombok.Setter;

@Getter
public enum WorkType {
    ATTENDANCE,   // 출근
    LEAVE,        // 퇴근
    VACATION,     // 휴가
    AM_HALF_DAY,  // 오전 반차
    PM_HALF_DAY,  // 오후 반차
    BUSINESS_TRIP // 출장
    ;

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
