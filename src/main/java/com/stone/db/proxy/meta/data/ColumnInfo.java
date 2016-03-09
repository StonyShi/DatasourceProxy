package com.stone.db.proxy.meta.data;

import java.io.Serializable;

/**
 *
 */
public class ColumnInfo implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private String columnLabel;
	private String columnName;
	private String columnClassName;
	private String columnTypeName;
	private Integer columnType;
	
	
	public ColumnInfo() {
	}
	public ColumnInfo(String columnName, String columnClassName,String columnTypeName) {
		this.columnName = columnName;
		this.columnClassName = columnClassName;
		this.columnTypeName = columnTypeName;
	}
	public String getColumnLabel() {
		return columnLabel;
	}
	public void setColumnLabel(String columnLabel) {
		this.columnLabel = columnLabel;
	}
	public String getColumnName() {
		return columnName;
	}
	public void setColumnName(String columnName) {
		this.columnName = columnName;
	}
	public String getColumnClassName() {
		return columnClassName;
	}
	public void setColumnClassName(String columnClassName) {
		this.columnClassName = columnClassName;
	}
	public String getColumnTypeName() {
		return columnTypeName;
	}
	public void setColumnTypeName(String columnTypeName) {
		this.columnTypeName = columnTypeName;
	}
	public Integer getColumnType() {
		return columnType;
	}
	public void setColumnType(Integer columnType) {
		this.columnType = columnType;
	}
	
	
}
