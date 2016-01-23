package com.stone.db.proxy.cache;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.AnnotationUtils;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by ShiHui on 2016/1/23.
 */
@Aspect
public class CacheManagerAspect {

    @Pointcut("@annotation(com.stone.db.proxy.cache.CacheableMap)")
    public void cachePointcut() {}


    @Around("cachePointcut()")
    public Object caching(final ProceedingJoinPoint pjp) throws Throwable {
        return cache(pjp);
    }

    protected Object cache(final ProceedingJoinPoint pjp) throws Throwable{
        log("Cache : %s#%s.",pjp.getSignature().getDeclaringTypeName(),pjp.getSignature().getName());

        MethodSignature signature = (MethodSignature) pjp.getSignature();
        Method method = signature.getMethod();

        CacheableMap cacheableMap = AnnotationUtils.findAnnotation(method,CacheableMap.class);
        if(cacheableMap == null){
            return pjp.proceed();
        }
        String cacheKey = cacheableMap.key();
        if(cacheKey == null || cacheKey.length() == 0){
            cacheKey = pjp.getSignature().getDeclaringTypeName() + "#" + pjp.getSignature().getName();
        }
        try{
            final Object result = caches.get(cacheKey);
            if (result != null) {
                log("Caching on method %s and key [%s] hit.", pjp.toShortString(), cacheKey);
                return result;
            }
        } catch (Exception ex) {
            warn(ex, "Caching on method %s and key [%s] aborted due to an error.", pjp.toShortString(), cacheKey);
            return pjp.proceed();
        }
        final Object result = pjp.proceed();
        try {
            caches.put(cacheKey,result);
        } catch (Exception ex) {
            warn(ex, "Caching on method %s and key [%s] aborted due to an error.", pjp.toShortString(), cacheKey);
        }
        return result;
    }


    public static final Map<String,Object> caches = Collections.synchronizedMap(new HashMap<String, Object>());

    public static void log(String msg,Object... args){
        System.out.println(String.format(msg,args));
    }
    public static void warn(Throwable tx,String msg,Object... args){
        System.out.println(tx.getMessage());
        log(msg,args);
    }

}
