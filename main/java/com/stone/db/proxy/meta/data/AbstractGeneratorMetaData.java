package com.stone.db.proxy.meta.data;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

public abstract class AbstractGeneratorMetaData implements GeneratorMetaData{

	protected DataSource dataSource;

	public AbstractGeneratorMetaData(DataSource dataSource) {
		super();
		this.dataSource = dataSource;
	}

	public DataSource getDataSource(){
		return this.dataSource;
	}

	public ResultSet query(PreparedStatement ps) throws SQLException{
		return ps.executeQuery();
	}
	public ResultSet query(Connection conn,String sql) throws SQLException {
		return query(conn.prepareStatement(sql));
	}
	public ResultSet query(Connection conn, String sql, Object[] params) throws SQLException {
		PreparedStatement ps = conn.prepareStatement(sql);
		fillStatement(ps, params);
		return ps.executeQuery();
	}
	public void fillStatement(PreparedStatement ps,Object[] params) throws SQLException{
		if(params == null) return;
		for (int i = 0; i < params.length; i++) {
			ps.setObject(i+1, params[i]);
		}
	}
	public PreparedStatement prepareStatement(Connection conn,String sql) throws SQLException{
		return conn.prepareStatement(sql);
	}
	private int[] batch(Connection conn, String sql, Object[][] params) throws SQLException {
		int[] rows = null;
		PreparedStatement stmt = null;
		try {
			stmt = this.prepareStatement(conn, sql);

			for (int i = 0; i < params.length; i++) {
				this.fillStatement(stmt, params[i]);
				stmt.addBatch();
			}
			rows = stmt.executeBatch();
		} finally {
			close(stmt);
		}
		return rows;
	}
	public ResultSetMetaData getResultSetMetaData(ResultSet rs) throws SQLException{
		return rs.getMetaData();
	}
	public static String generatorMapper(String jdbcColumn,String columnType){
		StringBuffer buffer = new StringBuffer();
		buffer.append("<result column=\"").append(jdbcColumn).append("\" ")
				.append("property=\"").append(generatorColumnName(jdbcColumn)).append("\" ")
				.append("jdbcType=\"").append(generatorColumnType(columnType)).append("\" />");
		return buffer.toString();
	}
	public static String generatorColumnType(String columnType){
		if(columnType.equals("BIGINTEGER")){
			return "INTEGER";
		}
		if(columnType.equals("INT")){
			return "INTEGER";
		}
		if(columnType.equals("BIGINT")){
			return "INTEGER";
		}
		if(columnType.equals("DOUBLE")){
			return "INTEGER";
		}
		return columnType;
	}
	// 首字母大写
	public static String capital(String str) {
		char[] array = str.toCharArray();
		if (String.valueOf(array[0]).matches("[A-Z]")) {
			return str;
		}
		array[0] -= 32;
		return String.valueOf(array);
	}
	public static String[] split(String str,String delimiter){
		return str.split(delimiter);
	}
	public static final String DEFAULT_TABLE_DELIMITER = "_";
	/**
	 *
	 * @param columnName default delimiter _
	 * @return
	 */
	public static String generatorColumnName(String columnName){
		return generatorColumnName(columnName, DEFAULT_TABLE_DELIMITER);
	}

	public static String generatorColumnName(String columnName,String delimiter){
		if(columnName.startsWith(delimiter)){
			columnName = columnName.substring(1);
		}
		return minuscules(capital(split(columnName, delimiter)));
	}
	public static String generatorTableName(String tableName,String delimiter){
		if(tableName.startsWith(delimiter)){
			tableName = tableName.substring(1);
		}
		return capital(split(tableName, delimiter));
	}
	public static String generatorTableName(String tableName){
		return generatorTableName(tableName, DEFAULT_TABLE_DELIMITER);
	}
	public static String generatorColumnClass(String columnClass){
		if(columnClass.equals("java.lang.Long")){
			return "java.lang.Integer";
		}
		if(columnClass.equals("java.lang.Double")){
			return "java.lang.Integer";
		}
		if(columnClass.equals("java.sql.Date")){
			return "java.util.Date";
		}
		if(columnClass.equals("java.sql.Timestamp")){
			return "java.util.Date";
		}
		return columnClass;
	}
	public static String capital(String[] strs) {
		StringBuffer buffer = new StringBuffer();
		for(String str: strs){
			buffer.append(capital(str));
		}
		return buffer.toString();
	}
	// 首字母小写
	public static String minuscules(String str){
		char[] array = str.toCharArray();
		if (String.valueOf(array[0]).matches("[a-z]")) {
			return str;
		}
		array[0] += 32;
		return String.valueOf(array);
	}

	public ResultSet getTables(DatabaseMetaData dbMeta) throws SQLException{
		return dbMeta.getTables(null, null, null, new String[]{"TABLE"});
	}
	public ResultSet getTables(Connection conn) throws SQLException{
		return conn.getMetaData().getTables(null, null, null, new String[]{"TABLE"});
	}
	public List<ColumnInfo> getColumnList(Connection conn, String tableSchema,String tableName) throws SQLException{
		List<ColumnInfo> columnList = null;
		ResultSetMetaData rsmd = getColumnMetaData(conn, tableName);
		columnList = getColumnInfoList(rsmd);
		return columnList;
	}
	@Override
	public List<ColumnInfo> getColumnInfoList(Connection conn, String sql) throws SQLException{
		List<ColumnInfo> columnList = null;
		ResultSetMetaData rsmd = doGetColumnMetaData(conn, sql);
		columnList = getColumnInfoList(rsmd);
		return columnList;
	}
	public List<ColumnInfo> getColumnInfoList(ResultSetMetaData rsmd) throws SQLException{
		List<ColumnInfo> columnList = new ArrayList<ColumnInfo>();
		for (int j = 0; j < rsmd.getColumnCount(); j++) {
			ColumnInfo columnInfo = new ColumnInfo();
			columnInfo.setColumnName(rsmd.getColumnName(j+1));
			columnInfo.setColumnClassName(rsmd.getColumnClassName(j+1));
			columnInfo.setColumnTypeName(rsmd.getColumnTypeName(j+1));
			columnInfo.setColumnLabel(rsmd.getColumnLabel(j+1));
			columnInfo.setColumnType(rsmd.getColumnType(j+1)); //java.sql.Types
			columnList.add(columnInfo);
		}
		return columnList;
	}
	public ResultSetMetaData getColumnMetaData(Connection conn,String tableName) throws SQLException{
		String querySql = "SELECT * FROM " + tableName;
		return doGetColumnMetaData(conn, querySql);
	}
	public ResultSetMetaData doGetColumnMetaData(Connection conn,String querySql) throws SQLException{
		ResultSetMetaData rsmd = null;
		ResultSet rset = null;
		try {
			rset = query(conn, querySql);
			rsmd = rset.getMetaData();
		} finally {
			close(rset);
		}
		return rsmd;
	}
	public static final String TABLE_CAT = "TABLE_CAT";
	public static final String TABLE_TYPE = "TABLE_TYPE";
	public static final String TABLE_NAME = "TABLE_NAME";
	public static final String TABLE_SCHEM = "TABLE_SCHEM";
	public static final String TABLE_COMMENT = "TABLE_COMMENT";

	public static final String COLUMN_NAME = "COLUMN_NAME";
	public static final String COLUMN_COMMENT = "COLUMN_COMMENT";
	public static final String COMMENTS = "COMMENTS";
	public static final String DATA_TYPE = "DATA_TYPE";

	/**
	 *
	 * @param conn
	 * @param tableSchema
	 * @return Map key = table_name（toLowerCase）,value = table_comment
	 * @throws SQLException
	 */
	public abstract Map<String,String> getTablesComment(Connection conn,String tableSchema) throws SQLException;
	/**
	 *
	 * @param conn
	 * @param tableName
	 * @return Map key = column_name（toLowerCase）,value = column_comment
	 * @throws SQLException
	 */
	public abstract Map<String,String> getColumnsComment(Connection conn, String tableSchema,String tableName) throws SQLException;

	public static boolean isEmpty(String v) {
		return v == null || v.length() == 0;
	}

	public static boolean isNotEmpty(String v) {
		return !isEmpty(v);
	}

	protected void close(Connection conn) throws SQLException {
		if(conn != null) conn.close();
	}

	protected void close(Statement stmt) throws SQLException {
		if(stmt != null) stmt.close();
	}

	protected void close(ResultSet rs) throws SQLException {
		if(rs != null) rs.close();
	}


	protected List<TableInfo> getTableInfoByResult(ResultSet tables)throws SQLException{
		List<TableInfo>  tablesList = new ArrayList<TableInfo>();
		while(tables.next()){
			tablesList.add(new TableInfo(tables.getString(TABLE_CAT),tables.getString(TABLE_NAME),tables.getString(TABLE_TYPE)));
		}
		return tablesList;
	}

	protected Map<String, String> getMapByResult(ResultSet rs, String key, String value)throws SQLException {
		Map<String, String> result = new HashMap<String, String>();
		while (rs.next()) {
			result.put(rs.getString(key).toLowerCase(), rs.getString(value));
		}
		return result;
	}

}
