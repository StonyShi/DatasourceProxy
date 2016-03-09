package com.stone.db.proxy.meta.data;

import java.io.Serializable;

public class TableInfo implements Serializable{

	private static final long serialVersionUID = 1L;

	private String tableCat; //表所属数据库
	private String tableSchem; //表所属用户名
	private String tableName; //表名
	private String tableType; //表类型
	private String remarks; //表备注

	/**
	 *
	 * @param tableCat 表所属数据库
	 * @param tableName 表名
	 */
	public TableInfo(String tableCat, String tableName) {
		super();
		this.tableCat = tableCat;
		this.tableName = tableName;
	}
	/**
	 *
	 * @param tableCat 表所属数据库
	 * @param tableName 表名
	 * @param tableType 表类型 TABLE,VIEW
	 */
	public TableInfo(String tableCat, String tableName, String tableType) {
		super();
		this.tableCat = tableCat;
		this.tableName = tableName;
		this.tableType = tableType;
	}

	public String getTableCat() {
		return tableCat;
	}
	public void setTableCat(String tableCat) {
		this.tableCat = tableCat;
	}
	public String getTableSchem() {
		return tableSchem;
	}
	public void setTableSchem(String tableSchem) {
		this.tableSchem = tableSchem;
	}
	public String getTableName() {
		return tableName;
	}
	public void setTableName(String tableName) {
		this.tableName = tableName;
	}
	public String getTableType() {
		return tableType;
	}
	public void setTableType(String tableType) {
		this.tableType = tableType;
	}
	public String getRemarks() {
		return remarks;
	}
	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}



}
