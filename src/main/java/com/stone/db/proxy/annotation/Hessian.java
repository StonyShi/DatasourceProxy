package com.stone.db.proxy.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
 
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Hessian {
 
    String description() default "";
 
    boolean overloadEnabled() default false; // 是否支持方法重载
     
    String uri(); // 用于服务端bean名称，也是客户端访问链接的后半部分 配置。如: /talentService
 
    Context context(); // 客户端访问链接前半部分配置 如 http://localhost:8004/remote


    enum Context {
        API_V2("api.v2.remote.url");

        private String remoteUrlConfigKey;

        private Context(String remoteUrlConfigKey) {
            this.remoteUrlConfigKey = remoteUrlConfigKey;
        }

        public String getRemoteUrl() {
            return System.getProperty(remoteUrlConfigKey, "http://127.0.0.1/remote");
        }
    }
}