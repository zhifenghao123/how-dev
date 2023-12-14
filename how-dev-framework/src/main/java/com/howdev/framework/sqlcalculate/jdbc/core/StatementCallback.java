package com.howdev.framework.sqlcalculate.jdbc.core;

import java.sql.SQLException;
import java.sql.Statement;

/**
 * StatementCallback class
 *
 * @author haozhifeng
 * @date 2023/12/11
 */
public interface StatementCallback<T> {
        /**
         * 创建表
         *
         * @param statement statement
         * @return:
         * @author: haozhifeng
         * @throws SQLException if thrown by a JDBC method, to be auto-converted
         */
        void doCreateTable(Statement statement) throws SQLException;
        /**
         * 给表insert数据以初始化
         *
         * @param statement statement
         * @return:
         * @author: haozhifeng
         * @throws SQLException if thrown by a JDBC method, to be auto-converted
         */
        void doInsertTable(Statement statement) throws SQLException;
        /**
         * 执行SQL查询，来获得数据计算结果
         *
         * @param statement statement
         * @return:
         * @author: haozhifeng
         * @throws SQLException if thrown by a JDBC method, to be auto-converted
         */
        T doInStatement(Statement statement) throws SQLException;
}
