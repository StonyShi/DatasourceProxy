package com.stone.db.proxy;

/**
 * Created by ShiHui on 2016/1/9.
 */
public class DataSourceProxyManager {

    private enum DataSourceType{
        MASTER,SLAVE
    }
    private static ThreadLocal<DataSourceType> holder = new ThreadLocal<DataSourceType>();

    public static boolean isMaster(){
        return DataSourceType.MASTER == holder.get();
    }
    public static boolean isNone(){
        return null == holder.get();
    }
    public static void markSlave() {
        holder.set(DataSourceType.SLAVE);
    }
    public static void markMaster(){
        holder.set(DataSourceType.MASTER);
    }
    public static void rest(){
        holder.set(null);
    }
}
