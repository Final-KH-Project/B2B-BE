package kh.gangnam.b2b.service.ServiceImpl;

import jakarta.transaction.Transactional;
import kh.gangnam.b2b.WebSocket.AlarmMessage;
import kh.gangnam.b2b.dto.alarm.AlarmDTO;
import kh.gangnam.b2b.dto.alarm.request.SaveAlarm;
import kh.gangnam.b2b.entity.alarm.Alarm;
import kh.gangnam.b2b.entity.auth.Employee;
import kh.gangnam.b2b.entity.board.Board;
import kh.gangnam.b2b.repository.AlarmRepository;
import kh.gangnam.b2b.repository.EmployeeRepository;
import kh.gangnam.b2b.repository.board.BoardRepository;
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


    // TODO 알람 전송 시 알람 데이터베이스 저장
    // TODO 알람을 수신한 사람은 알람에 접근할 수 있어야 함
    public void createReadStatusForAllUsers(Long boardId){
        employeeRepository.findAll().forEach(employee -> {
            alarmRepository.save(Alarm.builder()
                    .employee(employee)
                    .board(Board.builder().boardId(boardId).build())
                    .build());
        });
    } //DB에 알림 생성

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
        Employee employee = employeeRepository.findByLoginId(loginId);
                if(employee==null){
                    throw new RuntimeException("사용자를 찾을 수 없습니다.");
                }

        List<Alarm> alarms = alarmRepository.findByEmployee_EmployeeIdOrderByCreatedDateDesc(employee.getEmployeeId());
        return alarms.stream()
                .map(AlarmDTO::alarmDTO)
                .collect(Collectors.toList());
    }


    @Transactional
    @Override
    public void markAsRead(Long alarmId)  {
        Alarm alarm=alarmRepository.findById(alarmId)
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
        Employee employee = employeeRepository.findByLoginId(username); //세션 username == loginId

        if (employee != null) {
            Long userId = employee.getEmployeeId();
            return null;//alarmRepository.findByUserIdAndIsReadFalse(userId);
        } else {
            //로그인 ID에 해당하는 사용자가 없을 경우 : 빈 리스트 반환
            return Collections.emptyList();
        }
    }
}




