package kh.gangnam.b2b.exception;

import org.springframework.http.HttpStatus;

// DB 데이터 조회에 실패한 경우
public class NotFoundException extends CustomBusinessException {
    public NotFoundException(String msg) {
        super(msg, HttpStatus.NOT_FOUND);
    }
}