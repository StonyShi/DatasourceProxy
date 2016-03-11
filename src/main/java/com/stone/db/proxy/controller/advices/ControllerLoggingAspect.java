package com.stone.db.proxy.controller.advices;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Created by Stony on 2016/3/9.
 */
@Component
@Aspect
public class ControllerLoggingAspect {

    private static final Logger logger = LoggerFactory.getLogger(ControllerLoggingAspect.class);

    @Pointcut("within(@org.springframework.stereotype.Controller *)")
    public void controller() {}

    @Pointcut("execution(* *.*(..))")
    public void controllerMethod() {}


    @Pointcut("execution(* com.stone.db.proxy.controller.*.*(..))")
    public void pointController(){
    }
    @Pointcut("execution(* *.*(..)) && @annotation(org.springframework.web.bind.annotation.RequestMapping)")
    public void pointController2(){
    }

    @Around("controller() && controllerMethod()")
    public Object aroundControllerAdvice(ProceedingJoinPoint joinPoint)throws Throwable{
        return executeTimeLog(joinPoint);
    }
    private Object executeTimeLog(ProceedingJoinPoint joinPoint) throws Throwable {
        long begin = System.currentTimeMillis();
        try {
            return joinPoint.proceed(joinPoint.getArgs());
        } catch (Exception err) {
            throw err;
        } finally {
            long end = System.currentTimeMillis();
            Signature sig = joinPoint.getSignature();
            String className = sig.getDeclaringType().getName();
            String method = sig.getName();
            float execTime = (end - begin);
            logger.info(String.format("[%s#%s] Executed %f ms.", className, method, execTime));
        }
    }
}
