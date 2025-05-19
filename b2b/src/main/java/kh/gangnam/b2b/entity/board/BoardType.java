package kh.gangnam.b2b.entity.board;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum BoardType {
    NOTICE("notice","공지사항",101),
    EVENT("event","이벤트",102),
    FREE("free","자유게시판",103),
    ANONYM("anonym","익명게시판",104);

    final String lower;
    final String koType;
    final int typeNo;

    //숫자형 타입번호를 입력시 BoardType 생성시
    public static BoardType useTypeNo(int typeNo){
        for(BoardType type:BoardType.values()){
            if(type.getTypeNo()==typeNo)return type;
        }
        return null;
    }
}
