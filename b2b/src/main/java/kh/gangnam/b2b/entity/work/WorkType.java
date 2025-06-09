package kh.gangnam.b2b.entity.work;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum WorkType {
    ATTENDANCE("출근"),
    LEAVE("퇴근"),
    VACATION("휴가"),
    AM_HALF_DAY("오전 반차"),
    PM_HALF_DAY("오후 반차"),
    BUSINESS_TRIP("출장");

    private final String krName;

    @JsonCreator
    public static WorkType from(String value) {
        for (WorkType type : WorkType.values()) {
            if (type.name().equalsIgnoreCase(value) || type.getKrName().equals(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown workType: " + value);
    }

    @Override
    public String toString() {
        return krName;
    }
}
