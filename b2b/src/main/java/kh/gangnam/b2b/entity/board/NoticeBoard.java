package kh.gangnam.b2b.entity.board;

import jakarta.persistence.*;
import kh.gangnam.b2b.entity.auth.User;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
@Builder
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
    @JoinColumn(name = "user_id",nullable = false)
    private User user;

    @OneToMany(mappedBy="board", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ImgBoardPath> image = new ArrayList<>();

}
