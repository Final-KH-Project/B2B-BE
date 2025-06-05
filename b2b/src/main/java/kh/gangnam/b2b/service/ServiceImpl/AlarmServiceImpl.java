package kh.gangnam.b2b.service.ServiceImpl;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import kh.gangnam.b2b.WebSocket.AlarmMessage;
import kh.gangnam.b2b.dto.alarm.AlarmDTO;
import kh.gangnam.b2b.dto.alarm.AlarmType;
import kh.gangnam.b2b.dto.alarm.request.SaveAlarm;
import kh.gangnam.b2b.dto.board.response.CommentSaveResponse;
import kh.gangnam.b2b.entity.alarm.Alarm;
import kh.gangnam.b2b.entity.auth.Employee;
import kh.gangnam.b2b.entity.board.Board;
import kh.gangnam.b2b.entity.board.Comment;
import kh.gangnam.b2b.repository.AlarmRepository;
import kh.gangnam.b2b.repository.EmployeeRepository;
import kh.gangnam.b2b.repository.board.BoardRepository;
import kh.gangnam.b2b.repository.board.CommentRepository;
import kh.gangnam.b2b.service.AlarmService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;


import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AlarmServiceImpl implements AlarmService {

    // Alarm 서비스 비즈니스 로직 구현
    private final AlarmRepository alarmRepository;
    private final EmployeeRepository employeeRepository;
    private final BoardRepository boardRepository;
    private final SimpMessagingTemplate messagingTemplate; // 메세지 전송용
    private final CommentRepository commentRepository;


    // TODO 알람 전송 시 알람 데이터베이스 저장
    // TODO 알람을 수신한 사람은 알람에 접근할 수 있어야 함
    public void createReadStatusForAllUsers(Long boardId){
        employeeRepository.findAll().forEach(employee -> {
            alarmRepository.save(Alarm.builder()
                    .employee(employee)
                    .board(Board.builder().boardId(boardId).build())
                    .comment(null)  // 게시글 알람이므로 댓글은 null
                    .Type(AlarmType.BOARD_NEW)  // 게시글 알람 타입 설정
                    .build());
        });
    } //게시글 알람 DB 생성

    //내 게시글 댓글작성 알람 DB 생성
    public void createCommentAlarmForPostWriter(CommentSaveResponse savedComment, Long commentWriterId){
        try {
            // 1. 게시글 정보 조회해서 작성자 찾기
            Board board = boardRepository.findById(savedComment.getBoardId())
                    .orElseThrow(() -> new EntityNotFoundException("게시글을 찾을 수 없습니다."));


            // 2. 댓글 작성자와 게시글 작성자가 같으면 알림 생성하지 않음
            Long postWriterId = board.getAuthor().getEmployeeId();
            if (!postWriterId.equals(commentWriterId)) {
                // 3. 게시글 작성자에게 댓글 알림 생성
                Alarm savedAlarm = alarmRepository.save(Alarm.builder()
                        .employee(board.getAuthor())  // 게시글 작성자
                        .board(board)
                        .comment(Comment.builder().commentId(savedComment.getCommentId()).build())
                        .Type(AlarmType.COMMENT_NEW)  // 댓글 알람 타입 설정
                        .isRead(false)
                        .build());


                log.info("댓글 알림 생성 완료 - 게시글 작성자 ID: {}, 댓글 작성자 ID: {}",
                        postWriterId, commentWriterId);
            } else {
                log.info("자신의 게시글에 자신이 댓글 작성 - 알림 생성 안함");
            }

        } catch (Exception e) {
            log.error("댓글 알림 생성 중 오류 발생: {}", e.getMessage(), e);
        }
    }
/*
    //게시글 댓글 개수
    public AlarmDTO convertToDTO(Alarm alarm) {
        AlarmDTO dto = AlarmDTO.alarmDTO(alarm);

        System.out.println("알림 타입: " + alarm.getType()); // 1. 타입 확인
        System.out.println("댓글 존재 여부: " + (alarm.getComment() != null)); // 2. 댓글 확인

        // 댓글 알림인 경우에만 댓글 개수 설정
        if (alarm.getType() == AlarmType.COMMENT_NEW &&
                alarm.getComment() != null &&
                alarm.getComment().getBoard() != null) {


            System.out.println("조건문 통과!"); // 여기까지 와야 함
            Long commentCount = commentRepository.countByBoard_BoardId(
                    alarm.getComment().getBoard().getBoardId()
            );
            System.out.println("댓글 개수: " + commentCount); // 디버깅용
            dto.setCommentCount(commentCount.intValue());
        }

        return dto;
    }
    */

    //읽지 않은 알림 개수 리턴
    @Override
    public Integer unReadCount(Long employeeId) {
        return alarmRepository.countByEmployee_employeeIdAndIsReadFalse(employeeId);
    }

    @Override
    public Integer countUnReadBoard(String loginId) {
        return alarmRepository.countByEmployee_loginIdAndIsReadFalse(loginId);
    }

    //알람 목록 조회 리턴
    public List<AlarmDTO> getAlarmsByLoginId(String loginId) {
        Employee employee = employeeRepository.findByLoginId(loginId)
                .orElseThrow(() -> new RuntimeException("해당 사원이 없습니다."));

        List<Alarm> alarms = alarmRepository.findByEmployee_EmployeeIdOrderByCreatedDateDesc(employee.getEmployeeId());
        return alarms.stream()
                .map(AlarmDTO::alarmDTO)
                .collect(Collectors.toList());
    }



    @Override
    public void markAsRead(Long alarmId)  {
        Alarm alarm = alarmRepository.findById(alarmId)
                .orElseThrow(()->new IllegalArgumentException("알림이 존재하지 않습니다."));

        alarm.setRead(true);//읽음 처리
        alarmRepository.save(alarm);

        log.info("알림 읽음 처리 완료 - alarmId: {}", alarmId);
    }

    @Override
    public void markAllAsRead(Long employeeId) {
        int updateCount= alarmRepository.markAllAsReadByEmployeeId(employeeId);

        //읽음 처리 후 webSocket으로 메세지 전송
        AlarmMessage message=AlarmMessage.builder()
                .type("MARK_ALL_AS_READ")
                .message("모든 알림 읽음 처리 완료.")
                .build();

        messagingTemplate.convertAndSend("/topic/alarms", message);
    } // 알림 전체 읽음 처리



/*
    @Transactional
    public void createCommentAlarm(CommentSaveResponse savedComment) {
        Board board = boardRepository.findById(savedComment.getBoardId())
                .orElseThrow(() -> new IllegalArgumentException("게시글이 존재하지 않습니다."));

        Employee postAuthor = board.getAuthor();
        Long commenterId = savedComment.getAuthor().getAuthorId();

        if (Objects.equals(postAuthor.getEmployeeId(), commenterId)) {
            return; // 자기 자신에게는 알림 저장하지 않음
        }

        Alarm alarm = Alarm.builder()
                .employee(postAuthor)
                .board(board)
                .message("회원님의 게시글에 새 댓글이 등록되었습니다.")
                .type(AlarmType.COMMENT)
                .isRead(false)
                .build();

        alarmRepository.save(alarm);
    } //댓글 알림 생성
    */


    // test용으로 생성
    @Override
    public void save(Long boardId){
        employeeRepository.findAll().forEach(employee -> {
            alarmRepository.save(Alarm.builder()
                    .employee(employee)
                    .board(Board.builder().boardId(boardId).build())
                    .build());
        });
    }


    @Override
    public ResponseEntity<?> saveAlarm(SaveAlarm saveAlarm) {
        return null;
    }
    /*
    @Override
    public ResponseEntity<?> readAlarm(Long id) {
        return null;
    }
    */
    @Override
    public ResponseEntity<String> deleteAlarm(Long id) {
        return null;
    }

    //username -> LoginId 변환에 사용
    @Override
    public List<Alarm> getUnreadAlarmsByUsername(String username) {
        //세션 username == loginId
        Employee employee = employeeRepository.findByLoginId(username)
                .orElseThrow(() -> new RuntimeException("해당 사원이 없습니다."));

        if (employee != null) {
            Long userId = employee.getEmployeeId();
            return null;//alarmRepository.findByUserIdAndIsReadFalse(userId);
        } else {
            //로그인 ID에 해당하는 사용자가 없을 경우 : 빈 리스트 반환
            return Collections.emptyList();
        }
    }
}




