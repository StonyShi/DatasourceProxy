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
}
