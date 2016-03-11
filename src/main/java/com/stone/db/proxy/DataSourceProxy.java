package com.stone.db.proxy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.jdbc.datasource.AbstractDataSource;
import org.springframework.util.Assert;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by ShiHui on 2016/1/9.
 */
public class DataSourceProxy extends AbstractDataSource implements InitializingBean{

    private static Logger logger = LoggerFactory.getLogger(DataSourceProxy.class);

    private DataSource master;
    private List<DataSource> slaves;
    private int slavesCount = 0;
    private AtomicInteger slaveRequest = new AtomicInteger(1);

    public DataSource getMaster() {
        return master;
    }

    public void setMaster(DataSource master) {
        this.master = master;
    }

    public List<DataSource> getSlaves() {
        return slaves;
    }

    public void setSlave(DataSource slave) {
        if(getSlaves() == null){
            setSlaves(Collections.synchronizedList(new ArrayList<DataSource>()));
        }
        getSlaves().add(slave);
    }
    public void setSlaves(List<DataSource> slaves) {
        this.slaves = slaves;
    }
    private DataSource determineDataSource(){
        if(DataSourceProxyManager.isNone()){
            logger.debug(">>> isNone current determine db : {}", this.master);
            return this.master;
        }
        if(DataSourceProxyManager.isMaster()){
            logger.debug(">>> isMaster current determine db  : {}", this.master);
            return this.master;
        }
        return determineSlaveDataSource();
    }

    private DataSource determineSlaveDataSource() {
        int index = slaveRequest.incrementAndGet() % slavesCount;
        if(index < 0) index = - 0;
        DataSource ds = this.slaves.get(index);
        logger.debug(">>> isSlave current determine db  : {}", ds);
        return ds;
    }

    @Override
    public Connection getConnection() throws SQLException {
        logger.debug("Enter");
        return determineDataSource().getConnection();
    }

    @Override
    public Connection getConnection(String username, String password) throws SQLException {
        return determineDataSource().getConnection(username,password);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.notNull(this.master, "master is required");
        Assert.notNull(this.slaves, "slave/slaves is required");
        this.slavesCount = this.slaves.size();
    }
}
