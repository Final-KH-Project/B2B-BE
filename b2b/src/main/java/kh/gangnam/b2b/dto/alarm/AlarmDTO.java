package kh.gangnam.b2b.dto.alarm;

import kh.gangnam.b2b.entity.alarm.Alarm;
import kh.gangnam.b2b.repository.board.CommentRepository;
import lombok.*;

import java.time.LocalDateTime;

//to Dto - mapping
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AlarmDTO {
    private Long alarmId;
    private Long employeeId;
    private String authorName; //게시글 작성자
    private Long authorId;
    private Long boardId;
    private String title;
    private Long commentId;
    private Integer commentCount;
    //private String commentAuthorName; // 게시글의 댓글 작성자

    private AlarmType type;
    private LocalDateTime createdDate;

    private boolean isRead;


    public static AlarmDTO alarmDTO(Alarm alarm){ //변환용 메서드
        return AlarmDTO.builder()
                .alarmId(alarm.getAlarmId())
                .employeeId(alarm.getEmployee().getEmployeeId())
                .boardId(alarm.getBoard() != null ? alarm.getBoard().getBoardId() : null)
                .commentId(alarm.getComment() != null ? alarm.getComment().getCommentId() : null)

                .title(alarm.getBoard() != null ? alarm.getBoard().getTitle() : null)
                .authorId(alarm.getBoard().getAuthor().getEmployeeId())
                .authorName(alarm.getBoard().getAuthor().getName())
                .type(alarm.getType())
                //.commentContent(alarm.getComment() != null ? alarm.getComment().getComment() : null)
                .createdDate(alarm.getCreatedDate())

                .isRead(alarm.isRead())
                .build();
    }

    // 타입에 따라 작성자 ID 반환
    private static Long getAuthorId(Alarm alarm) {
        if (alarm.getType() == AlarmType.COMMENT_NEW && alarm.getComment() != null) {
            return alarm.getComment().getAuthor().getEmployeeId(); // 댓글 작성자 ID
        }
        return alarm.getBoard() != null ? alarm.getBoard().getAuthor().getEmployeeId() : null; // 게시글 작성자 ID
    }

    // 타입에 따라 작성자 이름 반환
    private static String getAuthorName(Alarm alarm) {
        if (alarm.getType() == AlarmType.COMMENT_NEW && alarm.getComment() != null) {
            return alarm.getComment().getAuthor().getName(); // 댓글 작성자 이름
        }
        return alarm.getBoard() != null ? alarm.getBoard().getAuthor().getName() : null; // 게시글 작성자 이름
    }
}
