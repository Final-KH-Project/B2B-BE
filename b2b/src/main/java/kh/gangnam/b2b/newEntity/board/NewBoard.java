package kh.gangnam.b2b.newEntity;

import jakarta.persistence.*;
import kh.gangnam.b2b.newDto.board.BoardType;

@Entity
public class NewBoard {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long boardId;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, columnDefinition = "text")
    private String content;

    @Enumerated(EnumType.STRING)
    private BoardType type;

    @ManyToOne
    @JoinColumn(name = "employee_id")
    private Employee author;
}
