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
}
