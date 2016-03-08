package com.stone.db.proxy.meta.data;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;


public class MysqlGeneratorMetaData extends AbstractGeneratorMetaData{

	public MysqlGeneratorMetaData(DataSource dataSource) {
		super(dataSource);
	}
	public Connection getConnection() throws SQLException{
		return getDataSource().getConnection();
	}
	@Override
	public List<TableInfo> getTableInfoList(Connection conn,String tableSchema) throws SQLException{
		List<TableInfo> tablesList;
		ResultSet tables = null;
		try {
			tables = getTables(conn);
			tablesList = getTableInfoByResult(tables);
		} finally{
			close(tables);
		}
		return tablesList;
	}

	/**
	 *
	 * @param conn
	 * @param tableSchema
	 * @return Map key = table_name（toLowerCase）,value = table_comment
	 * @throws SQLException
	 */
	@Override
	public Map<String,String> getTablesComment(Connection conn,String tableSchema) throws SQLException{
		Map<String, String> result = null;
		String sql = "SELECT TABLE_NAME, TABLE_COMMENT FROM information_schema.tables WHERE TABLE_TYPE LIKE '%TABLE%' AND TABLE_SCHEMA = ?";
		ResultSet rs = null;
		try {
			rs = query(conn, sql, new Object[] { tableSchema });
			result = getMapByResult(rs,TABLE_NAME,TABLE_COMMENT);
		} finally {
			close(rs);
		}
		return result;
	}
	/**
	 *
	 * @param conn
	 * @param tableName
	 * @return Map key = column_name（toLowerCase）,value = column_comment
	 * @throws SQLException
	 */
	@Override
	public Map<String,String> getColumnsComment(Connection conn, String tableSchema,String tableName) throws SQLException{
		String sql = "SELECT COLUMN_NAME, DATA_TYPE, COLUMN_COMMENT FROM information_schema.columns WHERE TABLE_SCHEMA = ? AND TABLE_NAME = ?";
		Map<String, String> result = null;
		ResultSet rs = null;
		try {
			rs = query(conn, sql, new Object[] { tableSchema, tableName });
			result = getMapByResult(rs,COLUMN_NAME,COLUMN_COMMENT);
		} finally {
			close(rs);
		}
		return result;
	}
}
