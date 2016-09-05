package com.stone.db.proxy;

import com.stone.db.proxy.model.Cat;
import com.stone.db.proxy.service.impl.CatMainService;
import com.stone.db.proxy.service.impl.CatService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.annotation.Repeat;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Date;

/**
 * <p>Created with IntelliJ IDEA. </p>
 * <p>User: Stony </p>
 * <p>Date: 2016/8/9 </p>
 * <p>Time: 15:58 </p>
 * <p>Version: 1.0 </p>
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations ={"classpath:spring/spring-ctx-app.xml"})
public class CatServiceTest {

    @javax.annotation.Resource
    CatService catService;

    @javax.annotation.Resource
    CatMainService catMainService;

    @Test
    public void test1(){
        catMainService.queryCat(1);
        Cat cat = new Cat();
        cat.setName("Test_"+System.currentTimeMillis());
        cat.setCreateDate(new Date());
        catMainService.insertCat(cat);
    }

    @Repeat(5)
    @Test(expected = Exception.class)
    public void test2(){
        catMainService.findCat();
    }

    @Test
    public void test3(){
        Cat cat = new Cat();
        cat.setName("Test_"+System.currentTimeMillis());
        cat.setCreateDate(new Date());
        catMainService.queryAndInsert(cat);
    }
    @Test
    public void test4(){
        Cat cat = new Cat();
        cat.setName("Test_"+System.currentTimeMillis());
        cat.setCreateDate(new Date());
        catMainService.insertAndQuery(cat);
    }



    @Test
    public void test5555(){
        catService.getCat(1);
        Cat cat = new Cat();
        cat.setName("Test_"+System.currentTimeMillis());
        cat.setCreateDate(new Date());
        catService.saveCat(cat);
    }
}
