package com.stone.db.proxy.listener;


import org.h2.api.ErrorCode;
import org.h2.engine.Constants;
import org.h2.store.fs.FileUtils;
import org.h2.tools.RunScript;
import org.h2.tools.Script;
import org.h2.util.JdbcUtils;
import org.h2.util.Tool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Created by Stony on 2016/3/14.
 */
public class DataSourceClusterInitListener implements ApplicationListener<ContextRefreshedEvent> {
    public final Logger logger = LoggerFactory.getLogger(this.getClass());
    String urlSource = null;
    String[] urlTargets = null;
    String user = "";
    String password = "";
    String serverList = null;
    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        logger.info("ContextRefreshedEvent : {}" + event.getApplicationContext().getApplicationName());
        if(event.getApplicationContext().getParent() == null){
            CreateCluster createCluster = new CreateCluster();
            try {
                createCluster.execute(this.urlSource,urlTargets,user,password,serverList);
            } catch (SQLException e) {
                logger.error("ContextRefreshedEvent create cluster error.",e);
            }
        }
    }
    class CreateCluster extends Tool{
        @Override
        public void runTool(String... args) throws SQLException {
        }
        /**
         * Creates a cluster.
         *
         * @param urlSource the database URL of the original database
         * @param urlTargets the database URL of the copy
         * @param user the user name
         * @param password the password
         * @param serverList the server list
         */
        public void execute(String urlSource, String[] urlTargets,
                            String user, String password, String serverList) throws SQLException {
            if(serverList == null || serverList.length() == 0){
                serverList = urlSource;
                for(String urlTarget : urlTargets){
                    serverList += ";";
                    serverList += urlTarget;
                }
            }
            for(String urlTarget : urlTargets){
                process(urlSource, urlTarget, user, password, serverList);
            }
        }
        private void process(String urlSource, String urlTarget,
                             String user, String password, String serverList) throws SQLException {
            Connection connSource = null, connTarget = null;
            Statement statSource = null, statTarget = null;
            String scriptFile = "backup.sql";
            try {
                org.h2.Driver.load();

                // verify that the database doesn't exist,
                // or if it exists (an old cluster instance), it is deleted
                boolean exists = true;
                try {
                    connTarget = DriverManager.getConnection(urlTarget +
                                    ";IFEXISTS=TRUE;CLUSTER=" + Constants.CLUSTERING_ENABLED,
                            user, password);
                    Statement stat = connTarget.createStatement();
                    stat.execute("DROP ALL OBJECTS DELETE FILES");
                    stat.close();
                    exists = false;
                    connTarget.close();
                } catch (SQLException e) {
                    if (e.getErrorCode() == ErrorCode.DATABASE_NOT_FOUND_1) {
                        // database does not exists yet - ok
                        exists = false;
                    } else {
                        throw e;
                    }
                }
                if (exists) {
                    throw new SQLException(
                            "Target database must not yet exist. Please delete it first: " +
                                    urlTarget);
                }

                // use cluster='' so connecting is possible
                // even if the cluster is enabled
                connSource = DriverManager.getConnection(urlSource +
                        ";CLUSTER=''", user, password);
                statSource = connSource.createStatement();

                // enable the exclusive mode and close other connections,
                // so that data can't change while restoring the second database
                statSource.execute("SET EXCLUSIVE 2");
                try {

                    // backup
                    Script script = new Script();
                    script.setOut(out);
                    Script.process(connSource, scriptFile, "", "");

                    // delete the target database and then restore
                    connTarget = DriverManager.getConnection(
                            urlTarget + ";CLUSTER=''", user, password);
                    statTarget = connTarget.createStatement();
                    statTarget.execute("DROP ALL OBJECTS DELETE FILES");
                    connTarget.close();

                    RunScript runScript = new RunScript();
                    runScript.setOut(out);
                    RunScript.execute(urlTarget, user, password, scriptFile, null, false);

                    connTarget = DriverManager.getConnection(urlTarget, user, password);
                    statTarget = connTarget.createStatement();

                    // set the cluster to the serverList on both databases
                    statSource.executeUpdate("SET CLUSTER '" + serverList + "'");
                    statTarget.executeUpdate("SET CLUSTER '" + serverList + "'");
                } finally {

                    // switch back to the regular mode
                    statSource.execute("SET EXCLUSIVE FALSE");
                }
            } finally {
                FileUtils.delete(scriptFile);
                JdbcUtils.closeSilently(statSource);
                JdbcUtils.closeSilently(statTarget);
                JdbcUtils.closeSilently(connSource);
                JdbcUtils.closeSilently(connTarget);
            }
        }
    }
    public void setUrlSource(String urlSource) {
        this.urlSource = urlSource;
    }

    public void setUrlTargets(String[] urlTargets) {
        this.urlTargets = urlTargets;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setServerList(String serverList) {
        this.serverList = serverList;
    }
}
