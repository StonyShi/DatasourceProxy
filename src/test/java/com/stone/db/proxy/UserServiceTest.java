package com.stone.db.proxy;

import com.alibaba.fastjson.JSON;
import com.stone.db.proxy.model.User;
import com.stone.db.proxy.service.impl.UserService;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.util.Log4jConfigurer;

import java.io.FileNotFoundException;
import java.util.List;

/**
 * Created by ShiHui on 2016/1/23.
 */
public class UserServiceTest {

    public static void main(String[] args) {
//        DOMConfigurator.configure("classpath:config/log4j.xml");

        try {
            Log4jConfigurer.initLogging("classpath:config/log4j.xml");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        String[] configLocations = {"classpath:spring/spring-ctx-app.xml","classpath:spring/spring-servlet.xml"};
        ClassPathXmlApplicationContext ac = new ClassPathXmlApplicationContext(configLocations);

        System.out.println(ac);


        UserService userService = (UserService) ac.getBean("userService");

        System.out.println("----------------------------BEGIN");


        List<User> users = userService.getUsers();
        System.out.println("users = " + JSON.toJSONString(users));

        System.out.println("----------------------------");

        users = userService.getUsers();
        System.out.println("users = " + JSON.toJSONString(users));

        System.out.println("----------------------------");


        users = userService.getUsers();
        System.out.println("users = " + JSON.toJSONString(users));

        System.out.println("----------------------------");


        User user = userService.getUser(4);
        System.out.println("user = " + JSON.toJSONString(user));
        System.out.println("----------------------------");

        user = userService.getUser(4);
        System.out.println("user = " + JSON.toJSONString(user));
        System.out.println("----------------------------");
    }
}
