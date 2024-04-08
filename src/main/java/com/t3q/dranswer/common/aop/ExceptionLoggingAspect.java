package com.t3q.dranswer.common.aop;


import com.t3q.dranswer.repository.LoggingRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;


@Slf4j
@Aspect
@Component

public class ExceptionLoggingAspect {

    @Autowired
    private LoggingRepository loggingRepository;

    //@Around("execution(* com.t3q.dranswer.exception..*.*(..))" )
    public Object logWebLayer(ProceedingJoinPoint joinPoint) throws Throwable {
        log.info(":Exception Aspext: start line");
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();

        //log.info("=======is there access_toke: {} =======", request.getHeader("content-type"));
        log.info("======= URL: {} =======",request.getRequestURL());

        log.info(":Exception Aspect: -> start joinpoint");
        Object result = joinPoint.proceed(); // Actual method execution
        log.info(":Exception Aspect: -> ended joinpoint");

        ResponseEntity<?> responseEntity = (ResponseEntity<?>) result;
        log.info("=======Response StatusCode : {} =======", responseEntity.getStatusCode());
        log.info("=======Response Body : {} =======", responseEntity.getBody().toString());
        log.info("=======Response Body: {} =======", responseEntity.getHeaders());

        log.info(":Exception Aspext: end line");
        return result;
    }
}
