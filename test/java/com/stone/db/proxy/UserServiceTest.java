package com.stone.db.proxy;

import com.alibaba.fastjson.JSON;
import com.stone.db.proxy.model.User;
import com.stone.db.proxy.service.UserService;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.XmlWebApplicationContext;

import java.util.List;

/**
 * Created by ShiHui on 2016/1/23.
 */
public class UserServiceTest {

    public static void main(String[] args) {
        String[] configLocations = {"classpath:spring/spring-ctx-context.xml"};
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
