package com.stone.db.proxy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static Logger logger = LoggerFactory.getLogger(DataSourceProxyTransactionManager.class);

    @Override
    protected Object doGetTransaction() {
        logger.debug(">>> doGetTransaction.");
        return super.doGetTransaction();
    }

    /**
     * @param transaction
     * @param definition PROPAGATION_REQUIRED || PROPAGATION_REQUIRES_NEW  || PROPAGATION_NESTED
     */
    @Override
    protected void doBegin(Object transaction, TransactionDefinition definition) {
        logger.info(">>> doBegin transaction : {}, definition : {}",transaction,definition);
        logger.debug(">>> TransactionDefinition[name = {}, level = {}, propagation = {}, propagation = {}, isReadOnly = {}]"
                ,definition.getName()
                ,getIsolationLevelName(definition.getIsolationLevel())
                ,getPropagationBehaviorName(definition.getPropagationBehavior())
                ,definition.isReadOnly());
        determineDataSource(definition);
        super.doBegin(transaction, definition);
    }
    /**
     * Initialize transaction synchronization as appropriate.
     */
    @Override
    protected void prepareSynchronization(DefaultTransactionStatus status, TransactionDefinition definition) {
        logger.debug(">>> prepareSynchronization status : {}, definition : {}", status, definition);
        determineDataSource(definition);
        super.prepareSynchronization(status, definition);
    }
    private void determineDataSource(TransactionDefinition definition){
        if(DataSourceProxyManager.isNone()){
            if(definition.isReadOnly()){
                DataSourceProxyManager.markSlave();
                logger.debug(">>> markSlave because readOnly = {}", definition.isReadOnly());
            }else{
                DataSourceProxyManager.markMaster();
                logger.debug(">>> markMaster because readOnly = {}", definition.isReadOnly());
            }
        }
    }
    @Override
    protected void doCommit(DefaultTransactionStatus status) {
        logger.debug(">>> doCommit status : {}", status);
        super.doCommit(status);
    }

    @Override
    protected void doRollback(DefaultTransactionStatus status) {
        logger.debug(">>> doRollback status : {}", status);
        super.doRollback(status);
    }

    @Override
    protected void doResume(Object transaction, Object suspendedResources) {
        logger.debug(">>> doResume transaction : {}, suspendedResources : {}" , transaction, suspendedResources);
        super.doResume(transaction, suspendedResources);
    }

    @Override
    protected Object doSuspend(Object transaction) {
        logger.debug(">>> doCommit transaction : {}", transaction);
        return super.doSuspend(transaction);
    }

    @Override
    protected void doCleanupAfterCompletion(Object transaction) {
        DataSourceProxyManager.rest();
        logger.debug(">>> doCleanupAfterCompletion rest");
        super.doCleanupAfterCompletion(transaction);
    }
    @Override
    protected boolean isExistingTransaction(Object transaction) {
        boolean isExisting = super.isExistingTransaction(transaction);
        logger.debug(">>> isExistingTransaction : {}", isExisting);
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
