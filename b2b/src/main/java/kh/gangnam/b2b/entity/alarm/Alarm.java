package kh.gangnam.b2b.entity.alarm;

import jakarta.persistence.*;
import kh.gangnam.b2b.entity.CreatedAt;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor // 기본 생성자 생성
@Entity
public class Alarm extends CreatedAt {

    @Id // 기본 키(PK)
    @GeneratedValue (strategy = GenerationType.AUTO) // 기본키 값 자동 생성
    //각 알림에 대한 아이디 부여, DB에 저장할 때 식별자로 사용
    private Long alarmId;

    //알림 받을 사용자
    @Column(nullable = false)
    private Long userId;

//    @Column(nullable = false)
//    private String password;

    @Column(nullable = false)
    private String category;

    @Column(nullable = false)
    private String pageUrl;

    //알림 내용
    @Column(nullable = false)
    private String content;

    //읽음 여부 처리
    @Column(nullable = false)
    private boolean isRead=false;


    public  Alarm(Long userId, String content){
        this.userId=userId;
        this.content=content;
    }

    public void markAsRead(){
        this.isRead=true;
    }

}
