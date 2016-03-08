package com.stone.db.proxy.meta.data;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

public class OracleGeneratorMetaData extends AbstractGeneratorMetaData{

	public OracleGeneratorMetaData(DataSource dataSource) {
		super(dataSource);
	}
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
	 * ALL_TAB_COMMENTS | DBA_TAB_COMMENTS | USER_TAB_COMMENTS
	 */
	@Override
	public Map<String, String> getTablesComment(Connection conn, String tableSchema) throws SQLException {
		Map<String, String> result = null;
		String sql = "SELECT TABLE_NAME,TABLE_TYPE, COMMENTS FROM USER_TAB_COMMENTS WHERE OWNER = ?";
		ResultSet rs = null;
		try {
			rs = query(conn, sql, new Object[] { tableSchema });
			result = getMapByResult(rs,TABLE_NAME,COMMENTS);
		} finally {
			close(rs);
		}
		return result;
	}
	/**
	 * ALL_COL_COMMENTS | DBA_COL_COMMENTS | USER_COL_COMMENTS
	 */
	@Override
	public Map<String, String> getColumnsComment(Connection conn, String tableSchema, String tableName) throws SQLException {
		String sql = "SELECT COLUMN_NAME, COMMENTS FROM USER_COL_COMMENTS WHERE OWNER = ? AND TABLE_NAME = ?";
		Map<String, String> result = null;
		ResultSet rs = null;
		try {
			rs = query(conn, sql,new Object[]{tableSchema,tableName});
			result = getMapByResult(rs,COLUMN_NAME,COMMENTS);
		} finally {
			close(rs);
		}
		return result;
	}



}
