package kh.gangnam.b2b.dto.employee;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Position {
    CEO("최고경영자"),
    EXECUTIVE("경영진(임원)"),
    MANAGER("매니저(부/팀장)"),
    TEAM_LEADER("팀장"),
    STAFF("일반 직원"),
    NEWBIE("신입");

    private final String krName;

    // 문자열이 유효한 Enum 값인지 체크
    public static boolean isValid(String value) {
        for (Position p : Position.values()) {
            if (p.name().equalsIgnoreCase(value)) {
                return true;
            }
        }
        return false;
    }

    // 문자열을 Enum으로 안전하게 변환 (없으면 예외)
    public static Position from(String value) {
        for (Position p : Position.values()) {
            if (p.name().equalsIgnoreCase(value) || p.getKrName().equals(value)) {
                return p;
            }
        }
        throw new IllegalArgumentException("허용되지 않은 직급입니다: " + value);
    }

    @Override
    public String toString() {
        return krName;
    }
}