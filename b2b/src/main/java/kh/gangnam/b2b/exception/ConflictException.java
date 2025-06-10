package kh.gangnam.b2b.exception;

import org.springframework.http.HttpStatus;

// 기존 데이터와 신규 데이터가 충돌난 경우
public class ConflictException extends CustomBusinessException {
    public ConflictException(String msg) {
        super(msg, HttpStatus.CONFLICT);
    }
}