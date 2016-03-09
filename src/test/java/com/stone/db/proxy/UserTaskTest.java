package com.stone.db.proxy;

import com.alibaba.fastjson.JSON;
import com.stone.db.proxy.model.User;
import com.stone.db.proxy.task.UserTask;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.Date;
import java.util.List;
import java.util.Random;

/**
 * Created by ShiHui on 2016/1/10.
 */
public class UserTaskTest {

    public static void main(String[] args) throws Exception {
        ClassPathXmlApplicationContext ac = new ClassPathXmlApplicationContext("classpath:spring/spring-ctx-app.xml");


        System.out.println(ac);

        UserTask userTask = (UserTask) ac.getBean("userTask");
        System.out.println("----------------------------BEGIN");
        User user = new User();
        Random random = new Random(100000);

        user.setName("Task_" + random.nextInt(10000));
        user.setBirthday(new Date(System.currentTimeMillis() - random.nextInt(100000)));
        System.out.println("addUser = " + userTask.addUser(user));

        user.setName("Task_" + random.nextInt(10000));
        user.setBirthday(new Date(System.currentTimeMillis() - random.nextInt(100000)));
        System.out.println("addUser = " + userTask.addUser(user));

        user.setName("Task_" + random.nextInt(10000));
        user.setBirthday(new Date(System.currentTimeMillis() - random.nextInt(100000)));
        System.out.println("addUser = " + userTask.addUser(user));

        System.out.println("----tx------------------------");
        //userTask.addTx();

        List<User> users = userTask.getUsers();
        System.out.println("users = " + JSON.toJSONString(users));

        System.out.println("----------------------------");
    }
}
