package com.stone.db.proxy.advices;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Created by Stony on 2016/3/9.
 */

//@Component
//@Aspect
public class ServiceMethodAspect {

    private static final Logger logger = LoggerFactory.getLogger(ServiceMethodAspect.class);

    @Pointcut("execution(* com.stone.db.proxy.service.impl..*.*(*))")
    public void servicePointcut(){}

    //&& args(xx)
    @AfterThrowing(pointcut = "servicePointcut()" ,throwing = "ex", argNames="joinPoint,ex")
    public void logAfterThrowing(JoinPoint joinPoint, Throwable ex){
        logger.info("logAfterThrowing.");
        logger.error("logAfterThrowing ",ex);
        logMethod(joinPoint);
    }

    @AfterReturning(pointcut = "servicePointcut()", returning = "val", argNames="joinPoint,val")
    public void logAfterReturning(JoinPoint joinPoint, Object val){
        logger.info("logAfterReturning.");
        logMethod(joinPoint);
    }
    @Before("execution(* com.stone.db.proxy.service.impl..*.*(*)) ")
    public void before(JoinPoint joinPoint){

    }
    //ProceedingJoinPoint is only supported for around advice
    @Around("execution(* com.stone.db.proxy.service.impl..*.*(*)) ")
    public void around(ProceedingJoinPoint joinPoint){

    }
    protected void logMethod(JoinPoint joinPoint){
        Signature sig = joinPoint.getSignature();
        String task = sig.getDeclaringType().getName() + "#" + sig.getName();
        if(logger.isDebugEnabled()){
            logger.debug(task);
        }
    }
}
