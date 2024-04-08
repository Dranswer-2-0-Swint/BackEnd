package com.t3q.dranswer.common.aop;



import com.t3q.dranswer.repository.LoggingRepository;

import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.util.ContentCachingResponseWrapper;
import org.springframework.web.util.WebUtils;
import reactor.core.publisher.Mono;

import java.util.Arrays;


@Slf4j
@Aspect
@Component

public class ControllerLoggingAspect {
    @Autowired
    private HttpServletRequest request;

    @Autowired
    private LoggingRepository loggingRepository;

    //@Around("execution(* com.t3q.dranswer.controller..*.*(..))" )
    //@Around("@within(org.springframework.web.bind.annotation.RestController)")
    public Object logWebLayer(ProceedingJoinPoint joinPoint) throws Throwable {
        log.info(":Controller Aspect: start line");
        // Pre-method execution: Log start or input parameters
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        log.info("=======is there access_toke: {} =======", request.getHeader("content-type"));
        log.info("======= URL: {} =======",request.getRequestURL());



        log.info(":Controller Aspect: -> start joinpoint");
        Object result = joinPoint.proceed(); // Actual method execution
        log.info(":Controller Aspect: -> ended joinpoint");


        ResponseEntity<?> responseEntity = (ResponseEntity<?>) result;
        log.info("=======Response StatusCode : {} =======", responseEntity.getStatusCode());
        log.info("=======Response Body : {} =======", responseEntity.getBody());
        log.info("=======Response Body: {} =======", responseEntity.getHeaders());

        log.info(":Controller Aspect: end line");


        return result;
    }
//////

}
