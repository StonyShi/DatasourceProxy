package com.stone.db.proxy.mapper;

import com.stone.db.proxy.model.Cat;
import com.stone.db.proxy.service.impl.CatService;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.Log4jConfigurer;
import org.springframework.web.context.WebApplicationContext;

import java.io.FileNotFoundException;
import java.util.Date;

import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

/**
 * Created by Stony on 2016/3/14.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(locations ={"classpath:spring/spring-ctx-app.xml", "classpath:spring/spring-servlet.xml"})
public class CatMapperTest {

    private MockMvc mockMvc;

    @SuppressWarnings("SpringJavaAutowiringInspection")
    @Autowired
    protected WebApplicationContext wac;

    @Autowired CatMapper catMapper;

    @Autowired
    CatService catService;

    @Before
    public void setup() {
        try {
            Log4jConfigurer.initLogging("classpath:config/log4j.xml");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        this.mockMvc = webAppContextSetup(this.wac).build();
    }
    @Test
    public void getCat(){
//        catMapper.getCatById(1);
        catService.getCat(2);
    }
    @Test
    public void saveCat(){
        Cat cat = new Cat();
        cat.setName("DouDou");
        cat.setCreateDate(new Date());
//        catMapper.insert(cat);
        catService.saveCat(cat);
    }
    @After
    public void after(){

    }
}
