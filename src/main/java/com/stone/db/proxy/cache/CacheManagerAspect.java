package com.stone.db.proxy.cache;

import org.apache.http.client.utils.DateUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.lang.reflect.Method;
import java.util.Date;

/**
 * Created by ShiHui on 2016/1/23.
 */
@Aspect
public class CacheManagerAspect {

    private CachezManager cachezManager;

    public CacheManagerAspect() {
        if(cachezManager == null){
            setCachezManager(new CachezMapManager());
        }
    }

    public CacheManagerAspect(CachezManager cachezManager) {
        setCachezManager(cachezManager);
    }
    public void setCachezManager(CachezManager cachezManager){
        this.cachezManager = cachezManager;
    }
    public CachezManager getCachezManager(){
        return this.cachezManager;

    }
    protected Cachez getCachez(){
        return getCachezManager().getCachez();
    }
    protected void set(String key,Object value){
        getCachez().set(key,value);
    }
    protected Object get(String key){
        return getCachez().get(key);
    }

    @Pointcut("@annotation(com.stone.db.proxy.cache.Cachezable)")
    public void cachePointcut() {}


    @Around("cachePointcut()")
    public Object caching(final ProceedingJoinPoint pjp) throws Throwable {
        return cache(pjp);
    }

    protected Object cache(final ProceedingJoinPoint pjp) throws Throwable{
        log("Enter Caching : %s#%s.",pjp.getSignature().getDeclaringTypeName(),pjp.getSignature().getName());

        MethodSignature signature = (MethodSignature) pjp.getSignature();
        Method method = signature.getMethod();

        Cachezable cachezable = AnnotationUtils.findAnnotation(method,Cachezable.class);
        if(cachezable == null){
            return pjp.proceed();
        }
        String cacheKey = processCacheKey(pjp, cachezable);
        try{
            final Object result = get(cacheKey);
            if (result != null) {
                log("Caching on method %s hit key [%s]", pjp.toShortString(), cacheKey);
                return result;
            }
        } catch (Exception ex) {
            warn(ex, "Caching on method %s hit key [%s] aborted due to an error.", pjp.toShortString(), cacheKey);
            return pjp.proceed();
        }
        final Object result = pjp.proceed();
        try {
            set(cacheKey,result);
            log("Caching on method %s put key [%s]", pjp.toShortString(), cacheKey);
        } catch (Exception ex) {
            warn(ex, "Caching on method %s put key [%s] aborted due to an error.", pjp.toShortString(), cacheKey);
        }
        return result;
    }



    public static final ExpressionParser EXPRESSION_PARSER = new SpelExpressionParser();
    public static void log(String msg,Object... args){
        System.out.println(String.format(msg,args));
    }
    public static void warn(Throwable tx,String msg,Object... args){
        System.out.println(tx.getMessage());
        log(msg,args);
    }

    public boolean isEmpty(String v){
        return v == null || v.length() == 0;
    }
    public boolean isNotEmpty(String v){
        return !isEmpty(v);
    }
    private String processCacheKey(final ProceedingJoinPoint pjp,Cachezable cachezable){
        String cacheKey = cachezable.key();
        MethodSignature signature = (MethodSignature) pjp.getSignature();
        String className = pjp.getSignature().getDeclaringTypeName();
        String methodName = pjp.getSignature().getName();
        if(isEmpty(cacheKey)){
            return className + "_" + methodName;
        }else{
            try {
                Object[] args = pjp.getArgs();
                String[] argNames = signature.getParameterNames();
                if(args != null && args.length > 0){
                    EvaluationContext context = new StandardEvaluationContext();
                    for(int i = 0; i < argNames.length; i++){
                        context.setVariable(argNames[i],args[i]);
                    }
                    Object obj = EXPRESSION_PARSER.parseExpression(cacheKey).getValue(context);
                    cacheKey = obj.toString();
                }
                String prefix = cachezable.prefix();
                if(isEmpty(prefix)){
                    Cachezable.PrefixType prefixType = cachezable.prefixType();
                    if(Cachezable.PrefixType.CLASS_METHOD_NAME == prefixType){
                        prefix = className + "_" + methodName + "_";
                    } else if (Cachezable.PrefixType.CLASS_NAME == prefixType){
                        prefix = className + "_";
                    } else if(Cachezable.PrefixType.METHOD_NAME == prefixType){
                        prefix = methodName + "_";
                    }
                }
                if(isNotEmpty(prefix)) {
                    cacheKey = prefix + cacheKey;
                }
                String suffix = cachezable.suffix();
                if(isEmpty(suffix)){
                    Cachezable.SuffixType suffixType = cachezable.suffixType();
                    if(Cachezable.SuffixType.DATE_NAME == suffixType){
                        suffix = DateUtils.formatDate(new Date(),"_yyyy_MM_dd");
                    }else if(Cachezable.SuffixType.TIME_NAME == suffixType){
                        suffix = DateUtils.formatDate(new Date(),"_HH_mm_ss");
                    }else if(Cachezable.SuffixType.DATE_TIME_NAME == suffixType){
                        suffix = DateUtils.formatDate(new Date(),"_yyyy_MM_dd_HH_mm_ss");
                    }
                }
                if(isNotEmpty(suffix)){
                    cacheKey += suffix;
                }
            } catch (Exception e) {
                return className + "_" + methodName;
            }
        }
        return cacheKey;
    }
}
