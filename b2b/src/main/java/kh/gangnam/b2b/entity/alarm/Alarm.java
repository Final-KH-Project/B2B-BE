package kh.gangnam.b2b.entity.alarm;

import jakarta.persistence.*;
import kh.gangnam.b2b.dto.alarm.AlarmType;
import kh.gangnam.b2b.entity.BaseTimeEntity;
import kh.gangnam.b2b.entity.auth.Employee;
import kh.gangnam.b2b.entity.board.Board;
import kh.gangnam.b2b.entity.board.Comment;
import lombok.*;


@Getter
@Setter
@NoArgsConstructor // 기본 생성자 생성
@Builder
@AllArgsConstructor
@Entity
public class Alarm extends BaseTimeEntity {

    @Id // 기본 키(PK)
    @GeneratedValue (strategy = GenerationType.IDENTITY) // 기본키 값 자동 생성
    //각 알림에 대한 아이디 부여, DB에 저장할 때 식별자로 사용
    private Long alarmId;

    //사원별로 게시판 알람 생성
    @ManyToOne (fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id" , nullable = false)
    private Employee employee;

    //게시판
    @ManyToOne()
    @JoinColumn(name = "board_id")
    private Board board;
    
    // 댓글 알림
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comment_id")
    private Comment comment;

    @Enumerated(EnumType.STRING)
    private AlarmType Type;


    //읽음 여부 처리
    @Column(nullable = false)
    @Builder.Default
    private boolean isRead=false;


    public void markAsRead (){
        this.isRead=true;
    }

}
