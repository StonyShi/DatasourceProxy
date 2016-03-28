package com.stone.db.proxy;

import com.alibaba.fastjson.JSON;
import com.stone.db.proxy.model.User;
import com.stone.db.proxy.service.impl.UserService;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by ShiHui on 2016/1/10.
 */
public class DataSourceProxyTest {

    public static final AtomicInteger sequence = new AtomicInteger(1000);
    public static void main(String[] args) throws InterruptedException {
        ClassPathXmlApplicationContext ac = new ClassPathXmlApplicationContext("classpath:spring/spring-ctx-app.xml");
        System.out.println(ac);

        UserService userService = (UserService) ac.getBean("userService");

        System.out.println("----------------------------BEGIN");
        User user = new User();
        Random random = new Random(100000);

        user.setName("YY_" + random.nextInt(10000));
        user.setBirthday(new Date(System.currentTimeMillis() - random.nextInt(100000)));
        System.out.println("addUser = " + userService.addUser(user));

        user.setName("YY_" + random.nextInt(10000));
        user.setBirthday(new Date(System.currentTimeMillis() - random.nextInt(100000)));
        System.out.println("addUser = " + userService.addUser(user));

        user.setName("YY_" + random.nextInt(10000));
        user.setBirthday(new Date(System.currentTimeMillis() - random.nextInt(100000)));
        System.out.println("saveUser = " + userService.saveUser(user));

//        System.out.println("----tx------------------------");
//        //userService.tx();
//
        List<User> users = userService.getUsers();
        System.out.println("users = " + JSON.toJSONString(users));
//
        System.out.println("----------------------------");

        user.setId(1000000 + sequence.incrementAndGet());
        user.setBirthday(new Date(System.currentTimeMillis() - random.nextInt(100000)));
        user.setName("YY_" + random.nextInt(10000));
        System.out.println("addUser = " + userService.addUser(user));

        users = userService.getUsers();
        System.out.println("users = " + JSON.toJSONString(users));
//
        users = userService.getUsers();
        System.out.println("users = " + JSON.toJSONString(users));
        System.out.println("----------------------------END");


        ExecutorService executorService = Executors.newCachedThreadPool();
        for(int i=0; i< 10; i++){
            TimeUnit.SECONDS.sleep(1);
            executorService.execute(new GetUserAddTask(ac));
        }
//        for(int i=0; i< 10; i++){
//            TimeUnit.SECONDS.sleep(1);
//            executorService.execute(new AddUserTask(ac));
//        }
//        for(int i=0; i< 10; i++){
//            TimeUnit.SECONDS.sleep(1);
//            executorService.execute(new GetUserTask(ac));
//        }
        executorService.shutdown();

    }
    static class AddUserTask implements Runnable{
        ClassPathXmlApplicationContext ac;

        public AddUserTask(ClassPathXmlApplicationContext ac) {
            this.ac = ac;
        }
        @Override
        public void run() {
            UserService userService = (UserService) ac.getBean("userService");

            System.out.println("AddUserTask----------------------------BEGIN");
            User user = new User();
            Random random = new Random(10000);
            user.setId(1000000 + sequence.incrementAndGet());
            user.setBirthday(new Date(System.currentTimeMillis() - random.nextInt(100000)));
            user.setName("YY_" + random.nextInt(10000));
            System.out.println("addUser = " + userService.addUser(user));
            List<User> users = userService.getUsers();
            System.out.println("users = " + JSON.toJSONString(users));
            System.out.println("AddUserTask----------------------------END");
        }
    }
    static class GetUserTask implements Runnable{
        ClassPathXmlApplicationContext ac;

        public GetUserTask(ClassPathXmlApplicationContext ac) {
            this.ac = ac;
        }
        @Override
        public void run() {
            System.out.println("GetUserTask----------------------------BEGIN");
            UserService userService = (UserService) ac.getBean("userService");
            List<User> users = userService.getUsers();
            System.out.println("users = " + JSON.toJSONString(users));
            System.out.println("GetUserTask----------------------------END");
        }
    }

    static class GetUserAddTask implements Runnable{
        ClassPathXmlApplicationContext ac;

        public GetUserAddTask(ClassPathXmlApplicationContext ac) {
            this.ac = ac;
        }
        @Override
        public void run() {
            System.out.println("GetUserAddTask----------------------------BEGIN");
            UserService userService = (UserService) ac.getBean("userService");
            User user = new User();
            Random random = new Random(10000);
            user.setBirthday(new Date(System.currentTimeMillis() - random.nextInt(100000)));
            user.setName("Yun_" + random.nextInt(10000));
            List<User> users = userService.getUsersAnd(user);
            System.out.println("users = " + JSON.toJSONString(users));
            System.out.println("GetUserAddTask----------------------------END");
        }
    }
}
