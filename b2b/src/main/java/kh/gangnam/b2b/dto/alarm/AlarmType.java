package kh.gangnam.b2b.dto.alarm;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AlarmType {
    BOARD_NEW("게시글 등록"),
    COMMENT_NEW("댓글 등록");


    //결재나 근태 관련 항목 추가

    private final String description;


}
