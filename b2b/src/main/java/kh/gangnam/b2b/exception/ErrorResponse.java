package kh.gangnam.b2b.exception;

import java.time.LocalDateTime;

public record ErrorResponse(
        LocalDateTime timestamp,
        String code,       // ex: "RESOURCE_NOT_FOUND"
        String message,    // ex: "사원 정보를 찾을 수 없음"
        String path        // ex: "/api/employees/123"
) {
    public ErrorResponse(String code, String path, String message) {
        this(LocalDateTime.now(), code, message, path);
    }
}