package kh.gangnam.b2b.dto.board.request;

import kh.gangnam.b2b.entity.auth.User;
import kh.gangnam.b2b.entity.board.Board;
import kh.gangnam.b2b.entity.board.BoardType;


public record SaveRequest(String title,
                          String content,
                          BoardType type,
                          Long authorId) {

    public Board toEntity(){
        User user=null;
        if(authorId!=null){
            user=new User();
            user.setUserId(authorId);
        }

        return Board.builder()
                .title(title).content(content).type(type)
                .author(user)
                .build();
    }
}
