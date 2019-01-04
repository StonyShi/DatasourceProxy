package com.stone.db.proxy.test;

import com.stone.db.proxy.test.db.Cat;
import com.stone.db.proxy.test.db.Dog;
import org.junit.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * <p>DatasourceProxy
 * <p>com.stone.db.proxy.test
 *
 * @author stony
 * @version 上午11:06
 * @since 2018/12/20
 */
public class FactoryBeanTest {

    @Test
    public void test_0(){
        ClassPathXmlApplicationContext cxt = new ClassPathXmlApplicationContext("classpath:test.xml");
        cxt.start();

        System.out.println(cxt.getBean("cat").getClass());
        System.out.println(cxt.getBean("cat"));
        System.out.println(cxt.getBean("dog"));



        cxt.getBean(Cat.class).say();

        ((Dog)cxt.getBean("dog")).say();

        cxt.stop();
    }

    @Test
    public void test_37(){
        ClassPathXmlApplicationContext cxt = new ClassPathXmlApplicationContext("classpath:test2.xml");
        cxt.start();

        System.out.println(cxt.getBeanDefinitionCount());

        System.out.println(cxt.getBean("cat").getClass());

        cxt.getBean(Cat.class).say();

        cxt.stop();
    }
}
