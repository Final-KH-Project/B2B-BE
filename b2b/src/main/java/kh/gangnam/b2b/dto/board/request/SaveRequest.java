package kh.gangnam.b2b.dto.board.request;

import kh.gangnam.b2b.entity.auth.Employee;
import kh.gangnam.b2b.entity.board.Board;
import kh.gangnam.b2b.entity.board.BoardType;


public record SaveRequest(String title,
                          String content,
                          BoardType type,
                          Long authorId) {

    public Board toEntity(){
        Employee employee=null;
        if(authorId!=null){
            employee=new Employee();
            employee.setEmployeeId(authorId);
        }

        return Board.builder()
                .title(title).content(content).type(type)
                .author(employee)
                .build();
    }
}
