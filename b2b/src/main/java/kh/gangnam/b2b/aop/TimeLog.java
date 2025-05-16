package kh.gangnam.b2b.aop;


import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@Slf4j
@Aspect
public class TimeLog {

    @Before("execution(* kh.gangnam.b2b.controller.*.*(..))")
    public void DateTime(JoinPoint joinPoint) {
        log.info("{}" , LocalDateTime.now());
    }

    // 컨트롤러와 서비스 모두를 대상으로 하는 포인트컷
    @Around("execution(* kh.gangnam.b2b.controller.*.*(..)) || execution(* kh.gangnam.b2b.service.ServiceImpl.*.*(..))")
    public Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        String className = joinPoint.getTarget().getClass().getSimpleName();
        String methodName = joinPoint.getSignature().getName();
        String packageName = joinPoint.getTarget().getClass().getPackage().getName();

        String type;


        if (packageName.contains("controller")) {
            type = "[controller]";
        } else if (packageName.contains("service")) {
            type = "   [service]";
        } else {
            type = "[not found]";
        }

        long start = System.currentTimeMillis();
        log.info("{}", type + " Start " + className + "." + methodName);
        Object proceed = joinPoint.proceed();
        long executionTime = System.currentTimeMillis() - start;
        log.info("{} >> Time:{}", type + " End " + className + "." + methodName, executionTime + " ms");

        return proceed;
    }
}

