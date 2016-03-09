package com.stone.db.proxy.cache;


import java.lang.annotation.*;

/**
 * Created by ShiHui on 2016/1/23.
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface CacheableMap {


    String key() default "";

    String unless() default "";

    String condition() default "";

    String prefix() default "";

    String suffix() default "";

    PrefixType prefixType() default PrefixType.NULL_NAME;

    SuffixType suffixType() default SuffixType.NULL_NAME;

    int expire() default -1;

    enum PrefixType{
        METHOD_NAME,CLASS_NAME,CLASS_METHOD_NAME,NULL_NAME
    }
    enum SuffixType{
        NULL_NAME,DATE_NAME,TIME_NAME,DATE_TIME_NAME
    }
}
