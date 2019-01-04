package com.stone.db.proxy.test.annotations;


import java.lang.annotation.*;

/**
 * <p>DatasourceProxy
 * <p>com.stone.db.proxy.test
 *
 * @author stony
 * @version 下午3:32
 * @since 2018/12/20
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface QuerySql {


    String value();
}
