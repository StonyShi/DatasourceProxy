package com.stone.db.proxy;

import com.stone.db.proxy.meta.data.ColumnInfo;
import com.stone.db.proxy.meta.data.MysqlGeneratorMetaData;
import com.stone.db.proxy.meta.data.TableInfo;

import javax.sql.DataSource;
import java.io.PrintWriter;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Created by Stony on 2016/3/8.
 */
public class MysqlGeneratorMetaDataTest extends MysqlGeneratorMetaData{


    public MysqlGeneratorMetaDataTest(DataSource dataSource) {
        super(dataSource);
    }

    public static void main(String[] args) throws SQLException {
        MysqlGeneratorMetaDataTest meta = new MysqlGeneratorMetaDataTest(new MysqlGeneratorMetaDataSource());
        Connection conn = meta.getConnection();
        try {
            System.out.println(conn);
            System.out.println(meta.generatorTableName("statistic_customer"));


//			meta.testAllTables(conn, meta);
            String sql = "CREATE TABLE statistic_customer ( id int, name varchar(80) ,birthday DATE )";
            PreparedStatement ps = conn.prepareStatement(sql);
            int update = ps.executeUpdate();
            ps.close();

            System.out.println("update = " + update);
            sql = "Select id,name,birthday FROM statistic_customer";
            meta.testQuerySql(conn,sql);
        } finally {
            conn.close();
        }
    }

    void testQuerySql(Connection conn, String sql) throws SQLException{
        System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
        List<ColumnInfo> list = getColumnInfoList(conn, sql);
        for (int i = 0; i < list.size(); i++) {
            ColumnInfo info = list.get(i);
            //@JsonSerialize(using = Date2StringSerializer.class)
            System.out.print(generatorColumnClass(info.getColumnClassName()));
            System.out.print(" " + generatorColumnName(info.getColumnLabel().toLowerCase()));
            if((generatorColumnClass(info.getColumnClassName())).contains("BigDecimal")){
                System.out.println(" = BigDecimal.ZERO;");
            }else if((generatorColumnClass(info.getColumnClassName())).contains("Integer")){
                System.out.println(" = 0;");
            }else{
                System.out.println(";");
            }
        }
        System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
        for (int i = 0; i < list.size(); i++) {
            ColumnInfo info = list.get(i);
            System.out.println(generatorMapper(info.getColumnLabel().toLowerCase(), info.getColumnTypeName()));

        }
    }

    void testAllTables(Connection conn, MysqlGeneratorMetaData meta)throws SQLException{
        String tableSchema = conn.getCatalog();
        System.out.println(">>>>>>>>>>>>>>>> getCatalog : " + tableSchema);
        DatabaseMetaData dbMetaData = conn.getMetaData();
        System.out.println(dbMetaData);
        ResultSet tables = meta.getTables(dbMetaData);
        Map<String,String> tablesComment = meta.getTablesComment(conn, tableSchema);
        List<TableInfo> tablesList = getTableInfosBySet(tables, tablesComment);
        showMetaData(tables);
        showTablesColumn(conn, tablesList);
        tables.close();
    }

    void showTablesColumn(Connection conn,List<TableInfo> tablesList) throws SQLException{
        for (int i = 0; i < tablesList.size(); i++) {
            TableInfo tableInfo = tablesList.get(i);
            System.out.println("TABLE_NAME : " + tableInfo.getTableName() + ", CLASS_NAME : " + generatorTableName(tableInfo.getTableName()));
            String querySql = "SELECT * FROM " + tableInfo.getTableName();
            ResultSet rset = query(conn,querySql);
            ResultSetMetaData rsmd = rset.getMetaData();
            rset.close();

            //ResultSetMetaData rsmd = getColumnMetaData(conn,tableInfo.getTableName()); //query(conn,querySql).getMetaData();
            Map<String,String> columnsComment = getColumnsComment(conn,tableInfo.getTableCat(), tableInfo.getTableName());
            List<ColumnInfo> columnList = new ArrayList<ColumnInfo>();
            System.out.println("SQL------------------------------");
            for (int j = 0; j < rsmd.getColumnCount(); j++) {
                String columnClass = rsmd.getColumnClassName(j+1);
                String columnName = rsmd.getColumnName(j+1);
                System.out.print(rsmd.getColumnTypeName(j+1));
                System.out.print(" " + columnName);
                System.out.print(",");
                System.out.println(" ## " + columnsComment.get(columnName));
            }
            System.out.println("JAVAModel------------------------------");
            for (int j = 0; j < rsmd.getColumnCount(); j++) {
                String columnClass = rsmd.getColumnClassName(j+1);
                String columnName = rsmd.getColumnName(j+1);
                System.out.print(generatorColumnClass(columnClass));
                System.out.print(" " + generatorColumnName(columnName));
                System.out.print(";");
                System.out.println(" // " + columnsComment.get(columnName));
//				System.out.print(", getColumnClassName " + rsmd.getColumnClassName(j+1));
//				System.out.print(", getColumnLabel " + rsmd.getColumnLabel(j+1));
//				System.out.print(", getColumnName " + rsmd.getColumnName(j+1));
//				System.out.print(", getColumnType " + rsmd.getColumnType(j+1));//java.sql.Types
//				System.out.println(", getColumnTypeName " + rsmd.getColumnTypeName(j+1));
                columnList.add(new ColumnInfo(rsmd.getColumnClassName(j+1), rsmd.getColumnName(j+1), rsmd.getColumnTypeName(j+1)));
            }
            System.out.println("MapperXML------------------------------");
            for (int j = 0; j < rsmd.getColumnCount(); j++) {
                String columnName = rsmd.getColumnName(j+1);
                String columnType = rsmd.getColumnTypeName(j+1);
                System.out.println(generatorMapper(columnName, columnType));
            }
        }
    }
    void showMetaData(ResultSet tables) throws SQLException{
        System.out.println("tableMeta------------------------------");
        ResultSetMetaData tableMeta = tables.getMetaData();
        for (int i = 0; i < tableMeta.getColumnCount(); i++) {
            String columnClass = tableMeta.getColumnClassName(i+1);
            String columnName = tableMeta.getColumnName(i+1).toLowerCase();
            System.out.print(generatorColumnClass(columnClass));
            System.out.print(" " + generatorColumnName(columnName));
            System.out.println(";");
        }
    }

    List<TableInfo> getTableInfosBySet(ResultSet tables, Map<String,String> tablesComment)throws SQLException {
        List<TableInfo> tablesList = new ArrayList<TableInfo>();
        while(tables.next()){
//			System.out.println("TABLE_NAME : " + tables.getString(TABLE_NAME));
            System.out.println("表名：" + tables.getString("TABLE_NAME") + "<br/>");
            System.out.println("表类型：" + tables.getString("TABLE_TYPE") + "<br/>");
            System.out.println("表所属数据库：" + tables.getString("TABLE_CAT") + "<br/>");
            System.out.println("表所属用户名：" + tables.getString("TABLE_SCHEM")+ "<br/>");
            //System.out.println("表备注：" + tables.getString("REMARKS") + "<br/>");
            System.out.println("表备注：" + tablesComment.get(tables.getString("TABLE_NAME").toLowerCase()) + "<br/>");
            System.out.println("------------------------------<br/>");
            tablesList.add(new TableInfo(tables.getString("TABLE_CAT"),tables.getString(TABLE_NAME),tables.getString("TABLE_TYPE")));
        }
        return tablesList;
    }

    static class MysqlGeneratorMetaDataSource implements DataSource{

        private static String driverClass = "org.hsqldb.jdbcDriver";
        private static String connectionURL = "jdbc:hsqldb:mem:master";
        private static String userId = "sa";
        private static String password = "";
        static {
            try {
                Class.forName(driverClass);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }

        @Override
        public Connection getConnection() throws SQLException {
            return DriverManager.getConnection(connectionURL, userId, password);
        }

        @Override
        public Connection getConnection(String username, String password) throws SQLException {
            return DriverManager.getConnection(connectionURL, username, password);
        }

        @Override
        public PrintWriter getLogWriter() throws SQLException {
            return null;
        }

        @Override
        public void setLogWriter(PrintWriter out) throws SQLException {

        }

        @Override
        public void setLoginTimeout(int seconds) throws SQLException {

        }

        @Override
        public int getLoginTimeout() throws SQLException {
            return 0;
        }

        @Override
        public Logger getParentLogger() throws SQLFeatureNotSupportedException {
            return null;
        }

        @Override
        public <T> T unwrap(Class<T> iface) throws SQLException {
            return null;
        }

        @Override
        public boolean isWrapperFor(Class<?> iface) throws SQLException {
            return false;
        }
    }
}
