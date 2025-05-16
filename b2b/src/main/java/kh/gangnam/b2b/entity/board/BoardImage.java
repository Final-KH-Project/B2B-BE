package kh.gangnam.b2b.entity.board;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="board_image")
public class BoardImage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long imgId;

    @Column(nullable = false)
    private String s3Path;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id",nullable = false)
    private NoticeBoard board;

}

