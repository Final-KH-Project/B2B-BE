package kh.gangnam.b2b.entity.board;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import kh.gangnam.b2b.entity.BaseTimeEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class FreeBoard extends BaseTimeEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long freeId;

    private String title;
    private String content;
    private String department;
    private String author; //user의 이름과 연동

    public FreeBoard(String title, String content, String department,String author) {
        this.title = title;
        this.content = content;
        this.department = department;
        this.author = author;
    }
}
