package com.stone.db.proxy.test;

import com.stone.db.proxy.test.annotations.UpdateSql;
import com.stone.db.proxy.test.db.Cat;
import com.stone.db.proxy.test.db.Dog;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * <p>DatasourceProxy
 * <p>com.stone.db.proxy.test
 *
 * @author stony
 * @version 下午1:37
 * @since 2018/12/20
 */
public class AnimalHandler implements InvocationHandler {


    @Override
    public Object invoke(Object proxy, final Method method, Object[] args) throws Throwable {
        if (Object.class.equals(method.getDeclaringClass())) {
            try {
                return method.invoke(this, args);
            } catch (Throwable t) {
                throw new RuntimeException(t);
            }
        }
//        System.out.println();
        Object instance;
        if(method.getDeclaringClass().getSimpleName().equals("Cat")) {
            instance = new Cat() {
                @Override
                public void say() {
                    UpdateSql updateSql = method.getDeclaredAnnotation(UpdateSql.class);
                    if(updateSql != null) {
                        System.out.println("execute: " + updateSql.value());
                    }
                    System.out.println("我是喵嘛");
                }
            };
        }else {
            instance = new Dog() {
                @Override
                public void say() {
                    UpdateSql updateSql = method.getDeclaredAnnotation(UpdateSql.class);
                    if(updateSql != null) {
                        System.out.println("execute: " + updateSql.value());
                    }
                    System.out.println("我是狗狗");
                }
            };
        }
        return method.invoke(instance, args);
    }
}
