package kh.gangnam.b2b.aop;


import jakarta.persistence.EntityNotFoundException;
import kh.gangnam.b2b.WebSocket.NotificationPublisher;
import kh.gangnam.b2b.dto.board.request.BoardSaveResponse;
import kh.gangnam.b2b.entity.auth.Employee;
import kh.gangnam.b2b.entity.board.BoardType;
import kh.gangnam.b2b.repository.AlarmRepository;
import kh.gangnam.b2b.repository.EmployeeRepository;
import kh.gangnam.b2b.service.ServiceImpl.AlarmServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;



/**
 * 게시글 생성 알림, 게시글 읽음 이벤트 처리 Aspect
 * 알림 메시지를 생성하고 RabbitMQ를 통해 전송합니다.
 */
@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
@Transactional
public class BoardAlarmAspect {

    //private final RabbitTemplate rabbitTemplate;
    private final AlarmServiceImpl alarmServiceImpl;
    private final AlarmRepository alarmRepository;
    private final EmployeeRepository employeeRepository;
    private final NotificationPublisher notificationPublisher;

    /*
    //RabbitMQ exchange,routingKey 설정
    @Value("${spring.rabbitmq.template.exchange}")
    private String exchange;
    @Value("${spring.rabbitmq.template.routing-key}")
    private String routingKey;
     */

    @Around("execution(* kh.gangnam.b2b.service.*.BoardServiceImpl.save*(..))")
    public Object handleNotifyEvent(ProceedingJoinPoint joinPoint) throws Throwable {
        // 메서드 실행
        Object result = joinPoint.proceed();

        if (result instanceof BoardSaveResponse savedPost) {
            // 공지사항인 경우에만 처리
            if (savedPost.getType() == BoardType.NOTICE) {
                log.info("공지사항 저장 감지: {}", savedPost.getTitle());

                // 1. 모든 사용자에 대한 읽음 상태 생성
                alarmServiceImpl.createReadStatusForAllUsers(savedPost.getBoardId());
                // 2. RabbitMQ로 알림 메시지 발행
                notificationPublisher.publishNoticeEvent(savedPost);
            }
        }

        return result;
    } // 공지에 대해서 모든 임직원에게 알림 발송



    @Around("execution(* kh.gangnam.b2b.service.*.BoardServiceImpl.getBoard(..))")
    public Object markAlarmAsRead(ProceedingJoinPoint joinPoint) throws Throwable {
        // 1. 게시글 ID 파라미터 추출
        Object[] args = joinPoint.getArgs();
        Long boardId = (Long) args[0];


        // 2. loginId 추출
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String loginId=authentication.getName();
        System.out.println("username>>>:"+loginId);

        // 3. loginId → employeeId 추출
        Employee employee = employeeRepository.findByLoginId(loginId);
        if (employee == null) {
            throw new EntityNotFoundException("해당 loginId를 가진 사용자가 없습니다.");
        }
        Long employeeId=employee.getEmployeeId();

        // 4. 게시글 조회 실행
        Object result = joinPoint.proceed();

        // 5. 알림 읽음 처리
        alarmRepository.markAsRead(employeeId, boardId);
        return result;
    } // 알림 읽음 처리



//    private void publishNoticeEvent(BoardSaveResponse savedPost) {
//       rabbitTemplate.convertAndSend(exchange,routingKey,savedPost);
//    } //메세지 RabbitMQ 발행

//    private void createReadStatusForAllUsers(Long boardId){
//        employeeRepository.findAll().forEach(employee -> {
//            alarmRepository.save(Alarm.builder()
//                            .employee(employee)
//                            .board(Board.builder().boardId(boardId).build())
//                    .build());
//        });
//    } //DB에 알림 생성

}
