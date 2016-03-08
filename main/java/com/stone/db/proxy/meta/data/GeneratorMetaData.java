package com.stone.db.proxy.meta.data;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public interface GeneratorMetaData {
	/**
	 * 
	 * @param conn
	 * @param tableSchema current database
	 * @return
	 * @throws SQLException
	 */
	List<TableInfo> getTableInfoList(Connection conn, String tableSchema) throws SQLException;
	
	List<ColumnInfo> getColumnInfoList(Connection conn, String sql)throws SQLException;
}
