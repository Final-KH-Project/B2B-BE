package kh.gangnam.b2b.dto.auth;

public enum Position {
    INTERN("인턴"),
    STAFF("사원"),
    ASSISTANT_MANAGER("주임"),
    ASSOCIATE_MANAGER("대리"),
    MANAGER("과장"),
    DEPUTY_GENERAL_MANAGER("차장"),
    GENERAL_MANAGER("부장"),
    DIRECTOR("이사"),
    EXECUTIVE_DIRECTOR("상무"),
    SENIOR_EXECUTIVE_DIRECTOR("전무"),
    PRESIDENT("사장"),
    CEO("대표이사");

    private final String displayName;

    Position(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    // 문자열로부터 Enum을 얻는 유틸 메서드 (대소문자 구분 없이)
    public static Position from(String value) {
        for (Position position : Position.values()) {
            if (position.name().equalsIgnoreCase(value) || position.displayName.equals(value)) {
                return position;
            }
        }
        return null;
    }
}