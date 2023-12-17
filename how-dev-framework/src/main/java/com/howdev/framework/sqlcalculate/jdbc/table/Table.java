package com.howdev.framework.sqlcalculate.jdbc.table;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @Table 注解
 * @author haozhifeng
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Table {
    /**
     * 表名
     * @param
     * @return:
     * @author: haozhifeng
     */
    String tableName();

    /**
     * 建表语句
     * @return
     * @author: haozhifeng
     */
    String createTableSql();
}
