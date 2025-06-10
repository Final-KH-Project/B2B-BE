package kh.gangnam.b2b.exception;

import org.springframework.http.HttpStatus;

// 잘못된 요청 데이터 예외처리
public class InvalidRequestException extends CustomBusinessException {
    public InvalidRequestException(String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }
}