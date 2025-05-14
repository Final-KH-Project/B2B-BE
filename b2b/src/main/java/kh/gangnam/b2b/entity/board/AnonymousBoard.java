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
public class AnonymousBoard extends BaseTimeEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long anonymousId;

    private String title;
    private String content;
    private String author; //user의 id와 연동

    public AnonymousBoard(String title, String content) {
        this.title = title;
        this.content = content;
    }
}
