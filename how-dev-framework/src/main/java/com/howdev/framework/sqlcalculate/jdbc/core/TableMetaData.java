package com.howdev.framework.sqlcalculate.jdbc.core;

import java.util.List;

/**
 * TableMetaData class
 *
 * @author haozhifeng
 * @date 2023/12/13
 */
public class TableMetaData {
    /**
     * 表名
     */
    private String tableName;
    /**
     *  表创建ddl语句
     */
    private String createTableSql;

    /**
     *  表数据插入sql
     */
    private List<String> insertDataSqlList;

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getCreateTableSql() {
        return createTableSql;
    }

    public void setCreateTableSql(String createTableSql) {
        this.createTableSql = createTableSql;
    }

    public List<String> getInsertDataSqlList() {
        return insertDataSqlList;
    }

    public void setInsertDataSqlList(List<String> insertDataSqlList) {
        this.insertDataSqlList = insertDataSqlList;
    }
}
