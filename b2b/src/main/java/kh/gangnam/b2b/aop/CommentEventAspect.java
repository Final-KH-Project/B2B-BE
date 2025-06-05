package kh.gangnam.b2b.aop;

import kh.gangnam.b2b.WebSocket.NotificationPublisher;
import kh.gangnam.b2b.dto.board.response.CommentSaveResponse;
import kh.gangnam.b2b.service.ServiceImpl.AlarmServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Component;


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
    private final AlarmServiceImpl alarmServiceImpl;
    private final NotificationPublisher notificationPublisher;


    @Around("execution(* kh.gangnam.b2b.service.*.BoardServiceImpl.saveComment(..))")
    public Object handleCommentNotification(ProceedingJoinPoint joinPoint) throws Throwable {
        // 메서드 파라미터에서 employeeId 추출 (댓글 작성자 ID)
        Object[] args = joinPoint.getArgs();
        Long commentWriterId = (Long) args[1]; // employeeId 파라미터
        log.info("댓글 작성자 ID: {}", commentWriterId);

        // 댓글 생성 메서드 실행
        Object result = joinPoint.proceed();


       if(result instanceof CommentSaveResponse savedComment) {
           log.info("댓글 생성 감지: 게시글 ID {}, 댓글 작성자 ID: {}",
                   savedComment.getBoardId(), commentWriterId);
           // 게시글 작성자에게만 알림 생성 (자신의 댓글에는 알림 안 보냄)
           alarmServiceImpl.createCommentAlarmForPostWriter(savedComment, commentWriterId);
           // 알림 발행 RabbitMQ
           notificationPublisher.publishCommentEvent(savedComment);
        }
        return result;
    }

}


