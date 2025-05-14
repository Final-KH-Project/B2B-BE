package kh.gangnam.b2b.newEntity;

import jakarta.persistence.*;

@Entity
public class BoardImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long imgId;

    @Column(nullable = false)
    private String s3Path;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "board_id")
    private NewBoard board;
}
