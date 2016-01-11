package com.stone.db.proxy;

import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.TransactionDefinition;

/**
 * Created by ShiHui on 2016/1/9.
 */
public class DataSourceProxyTransactionManager extends DataSourceTransactionManager {

    @Override
    protected void doBegin(Object transaction, TransactionDefinition definition) {
        System.out.println("------------------ doBegin");
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
