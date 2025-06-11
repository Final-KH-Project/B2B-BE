package kh.gangnam.b2b.exception.handler;

import jakarta.servlet.http.HttpServletRequest;
import kh.gangnam.b2b.exception.CustomBusinessException;
import kh.gangnam.b2b.exception.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * 인증 실패에 대한 예외 처리
     * @param e
     * @return
     */
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<String> handleAuthException(AuthenticationException e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body("인증 실패: " + e.getMessage());
    }
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Map<String, Object>> handleResponseStatusException(ResponseStatusException ex) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", ex.getStatusCode().value());
        body.put("error", ex.getReason());
        ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = servletRequestAttributes.getRequest();
        String path = request.getRequestURI();
        body.put("path", path);
        return new ResponseEntity<>(body, ex.getStatusCode());
    }

    // CustomBusinessException
    @ExceptionHandler(CustomBusinessException.class)
    public ResponseEntity<ErrorResponse> handleCustomBusinessException(CustomBusinessException ex, HttpServletRequest request) {
        log.warn("비즈니스 로직 예외 발생 ({}): {}", ex.getHttpStatus().value(), ex.getMessage());
        return new ResponseEntity<>(new ErrorResponse(
                "CUSTOM_ERROR",
                request.getRequestURI(),
                ex.getMessage()
        ), ex.getHttpStatus());
    }

    // 나머지 일반 예외 처리 (RuntimeException, Exception 등)
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> handleGenericRuntimeException(RuntimeException ex, HttpServletRequest request) {
        log.error("처리되지 않은 런타임 오류 발생: {}", ex.getMessage(), ex);
        return new ResponseEntity<>(
                new ErrorResponse(
                        "INTERNAL_SERVER_ERROR",
                        request.getRequestURI(),
                        "서버 처리 중 알 수 없는 오류가 발생했습니다. 잠시 후 다시 시도해주세요."
                ),
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleAllOtherExceptions(Exception ex, HttpServletRequest request) {
        log.error("예상치 못한 서버 오류: {}", ex.getMessage(), ex);
        return new ResponseEntity<>(
                new ErrorResponse(
                        "UNEXPECTED_ERROR",
                        request.getRequestURI(),
                        "예상치 못한 서버 오류입니다. 관리자에게 문의해주세요."
                ),
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }
}
