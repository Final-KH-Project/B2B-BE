package kh.gangnam.b2b.entity.board;

import jakarta.persistence.*;
import kh.gangnam.b2b.dto.board.request.UpdateRequest;
import kh.gangnam.b2b.entity.BaseTimeEntity;
import kh.gangnam.b2b.entity.auth.Employee;
import lombok.*;

@ToString
@Getter
@NoArgsConstructor
@Builder
@AllArgsConstructor
@Entity
public class Board extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long boardId;

    @Column(nullable = false)
    private String title;
    @Column(nullable = false, columnDefinition = "text")
    private String content;

    @Enumerated(EnumType.STRING)
    private BoardType type;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id")
    private Employee author;

    //수정후 수정된 Board를 리턴
    public Board update(UpdateRequest dto){
        this.title=dto.title();
        this.content=dto.content();
        return this;
    }

}
