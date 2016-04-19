package com.stone.db.proxy;

import com.alibaba.fastjson.JSON;
import com.stone.db.proxy.mapper.CatMapper;
import com.stone.db.proxy.model.User;
import com.stone.db.proxy.service.impl.CatService;
import com.stone.db.proxy.service.impl.UserService;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.test.annotation.Repeat;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Log4jConfigurer;
import org.springframework.web.context.WebApplicationContext;

import java.io.FileNotFoundException;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

/**
 * Created by ShiHui on 2016/1/23.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(locations ={"classpath:spring/spring-ctx-app.xml", "classpath:spring/spring-servlet.xml"})
@TransactionConfiguration(transactionManager = "transactionManager")
@Transactional
public class UserServiceTest {


    @SuppressWarnings("SpringJavaAutowiringInspection")

    @Autowired
    CatMapper catMapper;

    @Autowired
    CatService catService;

    @Autowired
    UserService userService;

    public static final AtomicInteger sequence = new AtomicInteger(18000);
    @Rollback(true)
    @Repeat(5)
    @Test
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public void queryMax(){
        System.out.println("-BEGIN queryMax ######################");
        System.out.println("getUsers ----------------------------");
        List<User> users = userService.getUsers();
        System.out.println("users = " + JSON.toJSONString(users));
    }
    @Rollback(true)
    @Repeat(5)
    @Test
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public void queryUser() {
        System.out.println("-BEGIN queryUser ######################");

        System.out.println("getUsers ----------------------------");
        List<User> users = userService.getUsers();
        System.out.println("users = " + JSON.toJSONString(users));

        System.out.println("getUser 4 ----------------------------");
        User user = userService.getUser(4);
        System.out.println("user = " + JSON.toJSONString(user));
    }
    @Rollback(true)
    @Repeat(3)
    @Test
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public void insertUser(){
        System.out.println("-BEGIN insertUser ######################");
        User user = new User();
        Random random = new Random(10000);
        user.setId(1000000 + sequence.incrementAndGet());
        user.setBirthday(new Date(System.currentTimeMillis() - random.nextInt(100000)));
        user.setName("YY_" + random.nextInt(10000));
        System.out.println("addUser = " + userService.addUser(user));
    }
}
