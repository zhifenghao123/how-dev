package com.howdev.framework.sqlcalculate.jdbc.table;

/**
 * EntityTableConvertible class
 *
 * @author haozhifeng
 * @date 2023/12/13
 */
public interface EntityTableConvertible {
    /**
     * 转换为insert sql
     * @return
     * @author haozhifeng
     */
    public String convertToInsertSql();
}
