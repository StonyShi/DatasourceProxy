package com.stone.db.proxy;

import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.DefaultTransactionStatus;

/**
 * 1.事务管理由 TransactionInterceptor 拦截，执行invoke
 * 2.调用 TransactionAspectSupport#invokeWithinTransaction 实现环绕通知
 * 3.getTransaction>>doGetTransaction>>isExistingTransaction{
 *      return handleExistingTransaction
 * }
 * 4.if>>[PROPAGATION_REQUIRED || PROPAGATION_REQUIRES_NEW  || PROPAGATION_NESTED]{
 *      doBegin 在此将创建Connection,如果Connection 为新创建，绑定到TransactionSynchronizationManager#bindResource(DataSource, ConnectionHolder);
 *      prepareTransactionStatus
 *      return
 * }
 * 5.prepareTransactionStatus 将当前事务事务绑定到本地线程
 * 如果第四步没有执行，Connection 将由 DataSourceUtils#doGetConnection 创建，绑定到TransactionSynchronizationManager#bindResource(DataSource, ConnectionHolder)
 * 6.执行ReflectiveMethodInvocation#proceed 如果抛出异常执行completeTransactionAfterThrowing处理
 * 7.cleanupTransactionInfo
 * 8.commitTransactionAfterReturning>TransactionManager#commit
 *
 * Created by ShiHui on 2016/1/9.
 */
public class DataSourceProxyTransactionManager extends DataSourceTransactionManager {

    @Override
    protected Object doGetTransaction() {
        System.out.println(">>> doGetTransaction.");
        return super.doGetTransaction();
    }

    /**
     * @param transaction
     * @param definition PROPAGATION_REQUIRED || PROPAGATION_REQUIRES_NEW  || PROPAGATION_NESTED
     */
    @Override
    protected void doBegin(Object transaction, TransactionDefinition definition) {
        System.out.println(">>> doBegin transaction : " + transaction + ", definition : " + definition);
        System.out.println("name = " +definition.getName()
                +", level = " + getIsolationLevelName(definition.getIsolationLevel())
                + ", propagation = " + getPropagationBehaviorName(definition.getPropagationBehavior())
                + ", isReadOnly = " + definition.isReadOnly());
        if(definition.isReadOnly()){
            DataSourceProxyManager.markSlave();
            System.out.println(">>> markSlave because readOnly = " + definition.isReadOnly());
        }else{
            DataSourceProxyManager.markMaster();
            System.out.println(">>> markMaster because readOnly = " + definition.isReadOnly());
        }
        super.doBegin(transaction, definition);
    }
    /**
     * Initialize transaction synchronization as appropriate.
     */
    @Override
    protected void prepareSynchronization(DefaultTransactionStatus status, TransactionDefinition definition) {
        System.out.println(">>> prepareSynchronization status : " + status + ", definition : " + definition);
        if(DataSourceProxyManager.isNone()){
            if(definition.isReadOnly()){
                DataSourceProxyManager.markSlave();
                System.out.println(">>> markSlave because readOnly = " + definition.isReadOnly());
            }else{
                DataSourceProxyManager.markMaster();
                System.out.println(">>> markMaster because readOnly = " + definition.isReadOnly());
            }
        }
        super.prepareSynchronization(status, definition);
    }

    @Override
    protected void doCommit(DefaultTransactionStatus status) {
        System.out.println(">>> doCommit status : " + status);
        super.doCommit(status);
    }

    @Override
    protected void doRollback(DefaultTransactionStatus status) {
        System.out.println(">>> doRollback status : " + status);
        super.doRollback(status);
    }

    @Override
    protected void doResume(Object transaction, Object suspendedResources) {
        System.out.println(">>> doResume transaction : " + transaction + ", suspendedResources : " + suspendedResources);
        super.doResume(transaction, suspendedResources);
    }

    @Override
    protected Object doSuspend(Object transaction) {
        System.out.println(">>> doCommit transaction : " + transaction);
        return super.doSuspend(transaction);
    }

    @Override
    protected void doCleanupAfterCompletion(Object transaction) {
        DataSourceProxyManager.rest();
        System.out.println(">>> doCleanupAfterCompletion rest");
        super.doCleanupAfterCompletion(transaction);
    }
    @Override
    protected boolean isExistingTransaction(Object transaction) {
        boolean isExisting = super.isExistingTransaction(transaction);
        System.out.println(">>> isExistingTransaction : " + isExisting);
        return isExisting;
    }

    private String getIsolationLevelName(int code){
        switch (code){
            case TransactionDefinition.ISOLATION_DEFAULT:
                return "ISOLATION_DEFAULT";
            case TransactionDefinition.ISOLATION_READ_COMMITTED:
                return "ISOLATION_READ_COMMITTED";
            case TransactionDefinition.ISOLATION_READ_UNCOMMITTED:
                return "ISOLATION_READ_UNCOMMITTED";
            case TransactionDefinition.ISOLATION_REPEATABLE_READ:
                return "ISOLATION_REPEATABLE_READ";
            case TransactionDefinition.ISOLATION_SERIALIZABLE:
                return "ISOLATION_SERIALIZABLE";
            default: return "ISOLATION_DEFAULT";
        }
    }
    private String getPropagationBehaviorName(int code){
        switch (code){
            case TransactionDefinition.PROPAGATION_MANDATORY:
                return "PROPAGATION_MANDATORY";
            case TransactionDefinition.PROPAGATION_NESTED:
                return "PROPAGATION_NESTED";
            case TransactionDefinition.PROPAGATION_NEVER:
                return "PROPAGATION_NEVER";
            case TransactionDefinition.PROPAGATION_NOT_SUPPORTED:
                return "PROPAGATION_NOT_SUPPORTED";
            case TransactionDefinition.PROPAGATION_REQUIRED:
                return "PROPAGATION_REQUIRED";
            case TransactionDefinition.PROPAGATION_REQUIRES_NEW:
                return "PROPAGATION_REQUIRES_NEW";
            case TransactionDefinition.PROPAGATION_SUPPORTS:
                return "PROPAGATION_SUPPORTS";
            default:
                return "PROPAGATION_SUPPORTS";
        }
    }
}
