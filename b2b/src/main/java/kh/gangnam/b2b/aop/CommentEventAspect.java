package kh.gangnam.b2b.aop;

import kh.gangnam.b2b.WebSocket.AlarmMessage;
import kh.gangnam.b2b.dto.board.response.CommentSaveResponse;
import kh.gangnam.b2b.entity.auth.Employee;
import kh.gangnam.b2b.entity.board.Board;
import kh.gangnam.b2b.repository.EmployeeRepository;
import kh.gangnam.b2b.repository.board.BoardRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * 게시글 댓글 이벤트 처리 Aspect
 * 알림 메시지를 생성하고 RabbitMQ를 통해 전송합니다.
 */
@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class CommentEventAspect {

    private final SimpMessageSendingOperations messagingTemplate;
    private final BoardRepository boardRepository;
    private final EmployeeRepository employeeRepository;


    @Around("execution(* kh.gangnam.b2b.service.*.BoardServiceImpl.save*(..))")
    public Object sendCommentAlarmToPostAuthor(ProceedingJoinPoint joinPoint) throws Throwable {
        // 댓글 저장 메서드 실행
        Object result = joinPoint.proceed();

        if (result instanceof CommentSaveResponse savedComment) {
            log.info("댓글 저장 감지, 댓글 ID: {}", savedComment.getCommentId());

            // 게시글 정보 조회
            Board board = boardRepository.findById(savedComment.getBoardId())
                    .orElse(null);
            if (board == null) {
                log.warn("댓글이 달린 게시글을 찾을 수 없습니다. boardId: {}", savedComment.getBoardId());
                return result;
            }

            // 게시글 작성자
            Employee postAuthor = board.getAuthor();
            Long postAuthorId = postAuthor.getEmployeeId();

            // 댓글 작성자 ID
            Long commenterId = savedComment.getAuthor().getAuthorId();

            // 게시글 작성자와 댓글 작성자가 다를 경우에만 알림 전송
            if (!Objects.equals(postAuthor.getEmployeeId(), commenterId)) {
                AlarmMessage message = AlarmMessage.builder()
                        .type("COMMENT")
                        .message("회원님의 게시글에 새 댓글이 등록되었습니다.")
                        .build();

                String destination = "/topic/alarms.user." + postAuthor.getLoginId();
                messagingTemplate.convertAndSend(destination, message);
                log.info("댓글 알림 전송 완료: {}", destination);
            }
        }

        return result;
    }

}
