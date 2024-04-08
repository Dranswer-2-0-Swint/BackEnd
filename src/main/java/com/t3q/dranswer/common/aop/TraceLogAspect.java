package com.t3q.dranswer.common.aop;

import com.t3q.dranswer.common.trace.LogTrace;
import com.t3q.dranswer.common.trace.TraceStatus;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Aspect
@Component
@RequiredArgsConstructor
public class TraceLogAspect {

    private final LogTrace logTrace;


    @Pointcut("execution(* com.t3q.dranswer..*Service*.*(..))")
    public void allService() {
    }

    ;

    @Pointcut("execution(* com.t3q.dranswer..*Repository*.*(..))")
    public void allRepository() {
    }

    ;

    @Pointcut("execution(* com.t3q.dranswer..*Controller*.*(..))")
    public void allController() {
    }

    ;

    @Pointcut("execution(* com.t3q.dranswer..*Exception*.*(..))")
    public void allException() {
    }

    ;



    @Around("allService() || allController() || allRepository() || allException()")
    public Object logTrace(ProceedingJoinPoint joinPoint) throws Throwable {

        TraceStatus status = null;

        try {
            status = logTrace.begin(joinPoint.getSignature().toShortString());


            Object result = joinPoint.proceed();


            logTrace.end(status);
            return result;
        } catch (Throwable e) {
            //e.printStackTrace();
            //logTrace.exception(status, e);
            throw e;
        }
    }

}