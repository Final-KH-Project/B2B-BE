package kh.gangnam.b2b.entity.board;


import jakarta.persistence.*;
import kh.gangnam.b2b.entity.auth.Employee;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="notice")
public class NoticeBoard {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Lob
    @Column(columnDefinition = "LONGTEXT",nullable = false)
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    @OneToMany(mappedBy="board", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BoardImage> image = new ArrayList<>();

    public void updateTitleAndContent(String title, String content) {
        this.title = title;
        this.content = content;
    }
}

