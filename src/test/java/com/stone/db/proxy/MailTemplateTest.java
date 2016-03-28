package com.stone.db.proxy;

import com.alibaba.fastjson.JSON;
import com.stone.db.proxy.model.User;
import com.stone.db.proxy.service.impl.UserService;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Created by Stony on 2016/3/8.
 */
public class MailTemplateTest {


    public static void main(String[] args) {
        String[] configLocations = {"classpath:spring/spring-ctx-app.xml"};
        ClassPathXmlApplicationContext ac = new ClassPathXmlApplicationContext(configLocations);

        System.out.println(ac);


        UserService userService = (UserService) ac.getBean("userService");


        System.out.println("----------------------------BEGIN");

        User user = userService.getUser(4);
        System.out.println("user = " + JSON.toJSONString(user));
        System.out.println("----------------------------");

        userService.register(user);

    }
}
