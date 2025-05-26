package kh.gangnam.b2b.dto.board.request;

import kh.gangnam.b2b.entity.auth.Employee;
import kh.gangnam.b2b.entity.board.Board;
import kh.gangnam.b2b.entity.board.BoardType;
import java.util.List;

public record SaveRequest(String title,
                          String content,
                          BoardType boardType,
                          List<String> imageUrls) {

    public Board toEntity(Employee entity){

        return Board.builder()
                .title(title).content(content).type(boardType)
                .author(entity)
                .build();
    }
}
