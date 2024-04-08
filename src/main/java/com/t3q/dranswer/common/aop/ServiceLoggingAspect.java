package com.t3q.dranswer.common.aop;

import com.t3q.dranswer.repository.LoggingRepository;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Slf4j
@Aspect
@Component
public class ServiceLoggingAspect {
    @Autowired
    private LoggingRepository loggingRepository;
   //@AfterReturning(pointcut = "execution(* com.t3q.dranswer.service.*.*(..))", returning = "result")
    public void beforeServiceMethod(JoinPoint joinPoint) {
        // Identify the service method being called
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        String methodName = methodSignature.getName();
        String serviceClassName = methodSignature.getDeclaringType().getSimpleName();

        // Inspect the call stack to find the controller that made the call
        StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
        String callerClassName = null;
        String callerMethodName = null;
        for (StackTraceElement stackTraceElement : stackTraceElements) {
            if (stackTraceElement.getClassName().contains(".controller.")) {
                callerClassName = stackTraceElement.getClassName();
                callerMethodName = stackTraceElement.getMethodName();
                break; // Assuming the first controller in the call stack is the caller
            }
        }

        if (callerClassName != null && callerMethodName != null) {
            // Simple class name extraction
            String simpleCallerClassName = callerClassName.substring(callerClassName.lastIndexOf('.') + 1);
            log.info(simpleCallerClassName + "의" + callerMethodName + "에서" + serviceClassName + "의" + methodName + "를 호출했습니다");
        } else {
            log.info("Could not determine the controller and method that called " + serviceClassName + "#" + methodName);
        }
    }
}
