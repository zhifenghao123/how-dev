package com.howdev.framework.sqlcalculate.jdbc.core;

import com.howdev.framework.sqlcalculate.jdbc.udf.UdfRegistrar;

import java.util.List;
import java.util.Map;

/**
 * JdbcOperations class
 *
 * @author haozhifeng
 * @date 2023/12/11
 */
public interface JdbcOperations {
    /**
     * execute
     *
     *
     * @param dbEngineCode dbEngineCode
     * @param udfRegistrar
     * @param statementCallback statementCallback
     * @return:
     * @author: haozhifeng
     */
    <T> T execute(String dbEngineCode, UdfRegistrar udfRegistrar, StatementCallback<T> statementCallback);

    /**
     * 批量执行
     * @param dbEngineCode 数据库引擎编码
     * @param udfRegistrar 自定义函数注册器
     * @param tableMetaDataList 数据库表元数据
     * @param querySqlList  sql
     * @return
     */
    Map<String, Object> queryForBatch(final String dbEngineCode, UdfRegistrar udfRegistrar,
                                      final  List<TableMetaData> tableMetaDataList,
                                      final List<String> querySqlList);

    /**
     * 批量执行
     * @param dbEngineCode 数据库引擎编码
     * @param udfRegistrar 自定义函数注册器
     * @param tableMetaDataContainer 数据库表元数据
     * @param querySqlList  sql
     * @return
     */
    Map<String, Object> queryForBatch(final String dbEngineCode, UdfRegistrar udfRegistrar,
                                      final  TableMetaDataContainer tableMetaDataContainer,
                                      final List<String> querySqlList);

}
