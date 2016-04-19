package com.stone.db.proxy;

import junit.framework.Assert;
import org.easymock.EasyMock;
import org.junit.Test;
import org.junit.*;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.util.Log4jConfigurer;

import javax.sql.DataSource;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

/**
 * @author Stony
 *         Created Date : 2016/4/18  14:45
 */


public class ArrayCopyTest {

    @Before
    public void setup() {
        try {
            Log4jConfigurer.initLogging("classpath:config/log4j.xml");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testMaster(){
        System.out.println("testMaster #########################################");
        DataSourceProxy dd = new DataSourceProxy();
        DataSource ds = EasyMock.createMock(DataSource.class);
        dd.setMaster(ds);
        System.out.println("getMasters : " + (dd.getMasters()));
        Assert.assertTrue(dd.getMasters().length == 1);


        DataSource ds2 = EasyMock.createMock(DataSource.class);
        dd.setMaster(ds2);
        System.out.println("getMasters : " + (dd.getMasters().length));
        Assert.assertTrue(dd.getMasters().length == 2);


    }
    @Test
    public void testMasters() {
        DataSourceProxy dd = new DataSourceProxy();
        System.out.println("testMasters #########################################");
        List<DataSource> dss = new ArrayList<DataSource>();
        for(int i = 0; i < 5; i++){
            DataSource ds_ = EasyMock.createMock(DataSource.class);
            dss.add(ds_);
        }
        dd.setMasters(dss);
        System.out.println("getMasters : " + (dd.getMasters().length));
        Assert.assertTrue(dd.getMasters().length == 5);
    }
    @Test
    public void testSlave(){
        System.out.println("testSlave #########################################");
        DataSourceProxy dd = new DataSourceProxy();
        DataSource ds = EasyMock.createMock(DataSource.class);
        dd.setSlave(ds);
        System.out.println("getSlaves : " + (dd.getSlaves()));
        Assert.assertTrue(dd.getSlaves().length == 1);
    }
    @Test
    public void testSlaves() {
        System.out.println("testSlaves #########################################");
        DataSourceProxy dd = new DataSourceProxy();
        List<DataSource> dss = new ArrayList<DataSource>();
        for(int i = 0; i < 6; i++){
            DataSource ds_ = EasyMock.createMock(DataSource.class);
            dss.add(ds_);
        }
        dd.setSlaves(dss);
        System.out.println("getSlaves : " + (dd.getSlaves().length));
        Assert.assertTrue(dd.getSlaves().length == 6);
    }

}
