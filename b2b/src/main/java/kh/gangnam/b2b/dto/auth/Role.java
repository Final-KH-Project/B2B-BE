package kh.gangnam.b2b.dto.auth;

public enum Role {
    ROLE_GENERAL, // 일반 사원 (부서 조회만 가능)
    ROLE_MANAGER, // 인사팀 매니저 (전체 부서 조회 + 인사 기능)
    ROLE_ADMIN    // 부장급 관리자 (자신의 부서 관리)

    // 문자열을 Enum으로 변환 (대소문자 구분 없이)
    public static Role from(String value) {
        for (Role role : Role.values()) {
            if (role.name().equalsIgnoreCase(value)) {
                return role;
            }
        }
        return null;
    }
}