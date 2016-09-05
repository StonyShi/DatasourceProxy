package com.stone.db.proxy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.NamedThreadLocal;
import org.springframework.transaction.support.ResourceHolderSupport;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by ShiHui on 2016/1/9.
 */
public class DataSourceProxyManager {
    private static final Logger logger = LoggerFactory.getLogger(DataSourceProxyManager.class);


    private enum DataSourceType{
        MASTER,SLAVE,ALWAYS_MASTER
    }
    private static ThreadLocal<DataSourceType> holder = new ThreadLocal<DataSourceType>();
    /** holder Current Thread master connection **/
    private static ThreadLocal<Map<Object, Connection>> masterConnectionHolder =
            new NamedThreadLocal<Map<Object, Connection>>("Current master transaction connection");

    /** holder Current Thread slave connection **/
    private static ThreadLocal<Map<Object, Connection>> slaveConnectionHolder =
            new NamedThreadLocal<Map<Object, Connection>>("Current slave transaction connection");

    private static final ThreadLocal<Map<Object, Boolean>> currentSuspendedTransactionActive =
            new NamedThreadLocal<Map<Object, Boolean>>("Current suspended transaction active");

    private static final ThreadLocal<Map<Object, Connection>> currentSuspendedConnection =
            new NamedThreadLocal<Map<Object, Connection>>("Current suspended transaction connection");


    private static final ThreadLocal<ProxyTransactionRequestHolder> currentTransactionRequestHolder =
            new NamedThreadLocal<ProxyTransactionRequestHolder>("Current transaction transaction request count");



    public static boolean isMaster(){
        return DataSourceType.ALWAYS_MASTER == holder.get() || DataSourceType.MASTER == holder.get();
    }
    public static boolean isNone(){
        return null == holder.get();
    }
    public static void markSlave() {
        if(DataSourceType.SLAVE == holder.get()) return;
        holder.set(DataSourceType.SLAVE);
    }
    public static void markMaster(){
        if(DataSourceType.MASTER == holder.get()) return;
        holder.set(DataSourceType.MASTER);
    }
    public static void rest(){
        holder.set(null);
    }
    public static void alwaysMaster(){
        holder.set(DataSourceType.ALWAYS_MASTER);
    }

    public static DataSourceType getType() {
        return holder.get();
    }
    public static void clearMasterAndSlaveConnection(Object key){
        //清理master Connection
        Map<Object, Connection> master = masterConnectionHolder.get();
        if(null != master){
            master.remove(key);
        }
        //清理slave Connection
        Map<Object, Connection> slave = slaveConnectionHolder.get();
        if(null != slave){
            slave.remove(key);
        }
    }
    public static Connection getMasterConnection(Object key) {
        Map<Object, Connection> map = masterConnectionHolder.get();
        // set ThreadLocal Map if none found
        if (map == null) {
            return null;
        }else{
            return map.get(key);
        }
    }
    public static void setMasterConnection(Object key, Connection conn) {
        Map<Object, Connection> map = masterConnectionHolder.get();
        // set ThreadLocal Map if none found
        if (map == null) {
            map = new HashMap<Object, Connection>();
            masterConnectionHolder.set(map);
        }
        Object oldValue = map.put(key, conn);
        logger.trace("保存Master连接 [{}] for key [{}] in map {}.", conn, key, map);
    }
    public static Connection getSlaveConnection(Object key) {
        Map<Object, Connection> map = slaveConnectionHolder.get();
        // set ThreadLocal Map if none found
        if (map == null) {
            return null;
        }else{
            return map.get(key);
        }
    }
    public static void setSlaveConnection(Object key,Connection conn) {
        Map<Object, Connection> map = slaveConnectionHolder.get();
        // set ThreadLocal Map if none found
        if (map == null) {
            map = new HashMap<Object, Connection>();
            slaveConnectionHolder.set(map);
        }
        Object oldValue = map.put(key, conn);
        logger.trace("保存Slave连接 [{}] for key [{}] in map {}.", conn, key, map);
    }

    public static void setSuspendedTransactionActive(Object key, boolean active) {
        Map<Object, Boolean> map = currentSuspendedTransactionActive.get();
        // set ThreadLocal Map if none found
        if (map == null) {
            map = new HashMap<Object, Boolean>();
            currentSuspendedTransactionActive.set(map);
        }
        Object oldValue = map.put(key, (active ? Boolean.TRUE : null));
        logger.trace("挂起事务活动 [{}] for key [{}] in map {}.", active,key, map);
    }
    public static boolean isSuspendedTransactionActive(Object key) {
        Map<Object, Boolean> map = currentSuspendedTransactionActive.get();
        boolean isAvtive = false;
        if(null == map){
            isAvtive =  false;
        }else{
            isAvtive =  (map.get(key) != null);
        }
        logger.debug("挂起事务活动 is [{}] for key [{}] in map {}.", isAvtive, map);
        return isAvtive;
    }
    public static boolean hasSuspendedTransactionActive(){
        Map<Object, Boolean> map = currentSuspendedTransactionActive.get();
        if(null == map){
            return false;
        }else{
            for(Map.Entry<Object, Boolean> e : map.entrySet()){
                if(e.getValue() != null){
                    //logger.debug("存在挂起事务活动 is [{}] for key [{}] in map {}.", true, map);
                    return true;
                }
            }
        }
        return false;
    }
    public static void setCurrentSuspendedConnection(Object key, Connection conn) {
        Map<Object, Connection> map = currentSuspendedConnection.get();
        // set ThreadLocal Map if none found
        if (map == null) {
            map = new HashMap<Object, Connection>();
            currentSuspendedConnection.set(map);
        }
        Object oldValue = map.put(key, conn);
        logger.trace("挂起事务连接 [{}] for key [{}] in map {}.", conn, key, map);
    }

    /**
     * 释放挂起连接
     * 清理挂起线程数据
     */
    public static void cleanupAfterCompletion(){
//        closeMasterAndSlaveConnection();
        logger.debug("保存挂起活动 {}.",currentSuspendedTransactionActive.get());
        logger.debug("保存挂起连接 {}.",currentSuspendedConnection.get());
        logger.debug("保存Master连接 {}.",masterConnectionHolder.get());
        logger.debug("保存Salve连接 {}.",slaveConnectionHolder.get());
        closeSuspendedConnection();
    }

    /**
     * 最后清理master/slave 保存连接
     */
   public static void cleanupAfterFinallyCompletion(){
       closeMasterAndSlaveConnection();
       slaveConnectionHolder.set(null);
       masterConnectionHolder.set(null);
       currentTransactionRequestHolder.set(null);
   }
    /**
     * 释放连接
     */
    public static void closeMasterAndSlaveConnection(){
        if(null != masterConnectionHolder.get()){
            closeConnectionByMap(masterConnectionHolder.get());
        }
        if(null != slaveConnectionHolder.get()){
            closeConnectionByMap(slaveConnectionHolder.get());
        }
    }

    /**
     *
     * @param map ThreadLocal
     */
    private static void closeConnectionByMap(Map<Object,Connection> map){
        if((null != map) && (!map.isEmpty())){
            try {
                Connection conn;
                for(Map.Entry<Object, Connection> e : map.entrySet()){
                    conn = e.getValue();
                    close(conn);
                }
            } catch (Throwable ex) {
                logger.debug("Could not close JDBC Connection after transaction", ex);
            }
        }
    }
    public static void commitFinallyConnection(){
        if(null != masterConnectionHolder.get()){
            commitConnectionByMap(masterConnectionHolder.get());
        }
        if(null != slaveConnectionHolder.get()){
            commitConnectionByMap(slaveConnectionHolder.get());
        }
    }
    static void commitConnectionByMap(Map<Object, Connection> map){
        if((null != map) && (!map.isEmpty())){
            try {
                Connection conn;
                for(Map.Entry<Object, Connection> e : map.entrySet()){
                    conn = e.getValue();
                    commit(conn);
                }
            } catch (Throwable ex) {
                logger.debug("Could not commit JDBC Connection after transaction", ex);
            }
        }
    }

    public static void rollbackFinallyConnection(){
        if(null != masterConnectionHolder.get()){
            rollbackConnectionByMap(masterConnectionHolder.get());
        }
        if(null != slaveConnectionHolder.get()){
            rollbackConnectionByMap(slaveConnectionHolder.get());
        }
    }
    static void rollbackConnectionByMap(Map<Object, Connection> map){
        if((null != map) && (!map.isEmpty())){
            try {
                Connection conn;
                for(Map.Entry<Object, Connection> e : map.entrySet()){
                    conn = e.getValue();
                    rollback(conn);
                }
            } catch (Throwable ex) {
                logger.debug("Could not rollback JDBC Connection after transaction", ex);
            }
        }
    }
    /**
     * 清理挂起事务连接
     * @param key datasource
     */
    public static void removeSuspendedConnection(Object key){
        Map<Object, Connection> map = currentSuspendedConnection.get();
        if((null != map) && (!map.isEmpty())){
            map.remove(key);
        }
    }
    public static void closeSuspendedConnection(){
        Map<Object, Connection> map = currentSuspendedConnection.get();
        if((null != map) && (!map.isEmpty())){
            try {
                Connection conn;
                for(Map.Entry<Object, Connection> e : map.entrySet()){
                    conn = e.getValue();
                    close(conn);
                }
                map = null;
                currentSuspendedTransactionActive.set(null);
                currentSuspendedConnection.set(null);
            } catch (Throwable ex) {
                logger.debug("Could not close JDBC Connection after transaction", ex);
            }
        }
    }
    public static void commitSuspendedConnection(){
        commitConnectionByMap(currentSuspendedConnection.get());
    }
    public static void rollbackSuspendedConnection(){
        rollbackConnectionByMap(currentSuspendedConnection.get());
    }

    public static void close(Connection conn) throws SQLException {
        if (conn != null && !conn.isClosed()) {
            logger.debug("释放 JDBC Connection [" + conn + "] after transaction");
            conn.close();
        }
    }
    public static void rollback(Connection conn) throws SQLException {
        if ((conn != null) && (!conn.isClosed()) && (!conn.getAutoCommit())) {
            logger.debug("回滚 JDBC Connection [" + conn + "] after transaction");
            conn.rollback();
        }
    }
    public static void commit(Connection conn) throws SQLException {
        if ((conn != null) && (!conn.isClosed()) && (!conn.getAutoCommit())) {
            logger.debug("提交 JDBC Connection [" + conn + "] after transaction");
            conn.commit();
        }
    }

    public static void requestedTransaction() {
        ProxyTransactionRequestHolder holder = currentTransactionRequestHolder.get();
        if(holder == null){
            currentTransactionRequestHolder.set(new ProxyTransactionRequestHolder(Thread.currentThread().getName()));
            holder = currentTransactionRequestHolder.get();
        }
        holder.requested();
    }

    public static void releasedTransaction() {
        ProxyTransactionRequestHolder holder = currentTransactionRequestHolder.get();
        if(holder != null){
            holder.released();
        }
        logger.debug("释放事务引用 {}",holder);
    }

    public static class ProxyConnectionHolder extends ResourceHolderSupport {
        Connection connection;

        public Connection getConnection() {
            return connection;
        }

        public ProxyConnectionHolder(Connection connection) {
            this.connection = connection;
        }
    }

    public static class ProxyTransactionRequestHolder {
        String name;
        int referenceCount = 0;
        public ProxyTransactionRequestHolder(String name) {
            this.name = name;
            referenceCount = 0;
        }
        public String getName() {
            return name;
        }
        public int getReferenceCount() {
            return referenceCount;
        }
        public void requested() {
            this.referenceCount++;
        }
        public void released() {
            this.referenceCount--;
        }
        @Override
        public String toString() {
            return "ProxyTransactionRequestHolder{" +
                    "referenceCount='" + referenceCount + '\'' +
                    "name='" + name + '\'' +
                    '}';
        }
    }
}
