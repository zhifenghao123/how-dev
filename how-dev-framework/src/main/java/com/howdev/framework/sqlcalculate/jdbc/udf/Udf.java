package com.howdev.framework.sqlcalculate.jdbc.udf;

import com.howdev.framework.sqlcalculate.jdbc.enumeration.DbEngineEnum;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

/**
 * Udf class
 *
 * @author haozhifeng
 * @date 2023/12/11
 */
public interface Udf {
    /**
     * 获取UDF名称
     * @return
     */
    String getUdfName();
    /**
     * 注册UDF
     *
     * @param connection   连接
     * @param functionName 函数名
     * @return:
     * @throws: SQLException
     * @author: haozhifeng
     */
    void registerUdfBySelf(Connection connection, String functionName) throws SQLException;

    /**
     * 获取支持的数据库引擎
     * @return
     */
    List<DbEngineEnum> getSupportedDbEngines();
}
