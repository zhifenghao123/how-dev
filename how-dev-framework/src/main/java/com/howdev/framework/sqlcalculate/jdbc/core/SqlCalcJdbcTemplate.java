package com.howdev.framework.sqlcalculate.jdbc.core;

import com.howdev.framework.sqlcalculate.jdbc.datasource.DataSourceUtils;
import com.howdev.framework.sqlcalculate.jdbc.enumeration.SqlCalculateErrorCode;
import com.howdev.framework.sqlcalculate.jdbc.udf.UdfRegistrar;
import com.howdev.framework.sqlcalculate.jdbc.util.LogUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.ColumnMapRowMapper;
import org.springframework.jdbc.support.JdbcUtils;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * SqlCalcJdbcTemplate class
 *
 * @author haozhifeng
 * @date 2023/12/11
 */
public class SqlCalcJdbcTemplate implements JdbcOperations {
    private static final Logger LOGGER = LoggerFactory.getLogger(SqlCalcJdbcTemplate.class);

    @Override
    public <T> T execute(String dbEngineCode, UdfRegistrar udfRegistrar,
                         StatementCallback<T> statementCallback) {
        long startTime1 = System.currentTimeMillis();
        // 创建数据库连接
        Connection connection = DataSourceUtils.createConnection(dbEngineCode);
        long startTime2 = System.currentTimeMillis();
        LOGGER.info("SqlCalcJdbcTemplate#execute, stage={}, costs={}", "createConnection", startTime2 - startTime1);

        // 注册udf
        DataSourceUtils.registerUdf(connection, udfRegistrar);
        long startTime3 = System.currentTimeMillis();
        LOGGER.info("SqlCalcJdbcTemplate#execute, stage={}, costs={}", "registerUdf", startTime3 - startTime2);

        Statement stmt = null;
        T result = null;
        try {
            stmt = connection.createStatement();

            // 创建表
            statementCallback.doCreateTable(stmt);
            long startTime4 = System.currentTimeMillis();
            LOGGER.info("SqlCalcJdbcTemplate#execute, stage={}, costs={}", "doCreateTable", startTime4 - startTime3);

            // 初始化表数据，向表insert数据
            statementCallback.doInsertTable(stmt);
            long startTime5 = System.currentTimeMillis();
            LOGGER.info("SqlCalcJdbcTemplate#execute, stage={}, costs={}", "doInsertTable", startTime5 - startTime4);

            // 执行Sql化查询
            result = statementCallback.doInStatement(stmt);
            long startTime6 = System.currentTimeMillis();
            LOGGER.info("SqlCalcJdbcTemplate#execute, stage={}, costs={}", "doInStatement", startTime6 - startTime5);

        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            JdbcUtils.closeStatement(stmt);
            DataSourceUtils.closeConnection(connection);
        }
        return result;
    }

    @Override
    public Map<String, Object> queryForBatch(String dbEngineCode, UdfRegistrar udfRegistrar,
                                             List<TableMetaData> tableMetaDataList,
                                             List<String> querySqlList) {
        class QueryStatementCallback implements StatementCallback<Map<String, Object>> {

            @Override
            public void doCreateTable(Statement statement) throws SQLException {
                if (CollectionUtils.isEmpty(tableMetaDataList)) {
                    return;
                }

                for (TableMetaData tableMetaData : tableMetaDataList) {
                    statement.executeUpdate(tableMetaData.getCreateTableSql());
                }

            }

            @Override
            public void doInsertTable(Statement statement) {
                if (CollectionUtils.isEmpty(tableMetaDataList)) {
                    return;
                }
                for (TableMetaData tableMetaData : tableMetaDataList) {
                    for (String insertSql : tableMetaData.getInsertDataSqlList()) {
                        try {
                            statement.executeUpdate(insertSql);
                        } catch (Exception ex) {
                            LOGGER.error("logId: {}, insertSql : {}, doInsertTable error:{}, ", LogUtil.getLogId(),
                                    insertSql, ex);
                        }
                    }
                }

            }

            @Override
            public Map<String, Object> doInStatement(Statement statement) {
                if (CollectionUtils.isEmpty(querySqlList)) {
                    return null;
                }
                ResultSet resultSet = null;
                Map<String, Object> resultMap = new HashMap<>();
                try {


                    for (String querySql : querySqlList) {
                        try {
                            long start = System.currentTimeMillis();

                            // 执行sql
                            resultSet = statement.executeQuery(querySql);

                            // 解析sql执行结果
                            ColumnMapRowMapper columnMapRowMapper = new ColumnMapRowMapper();

                            Map<String, Object> result = new HashMap<>();

                            while (resultSet.next()) {
                                result = columnMapRowMapper.mapRow(resultSet, 0);
                                resultMap.putAll(result);
                            }
                            LOGGER.info("SqlCalcJdbcTemplate#execute, stage={}, costs={}",
                                    "executeSql-" + result.keySet().toString(), System.currentTimeMillis() - start);
                        } catch (Exception e) {
                            // sql执行异常时返回执行失败
                            LOGGER.error("执行计算sql时发生异常, sql:{}, errorcode = {}", querySql,
                                    SqlCalculateErrorCode.EXE_QUERY_SQL_ERROR.getCode(), e);
                        }
                    }
                } finally {
                    JdbcUtils.closeResultSet(resultSet);
                }
                return resultMap;
            }
        }

        return this.execute(dbEngineCode, udfRegistrar, new QueryStatementCallback());
    }

    @Override
    public Map<String, Object> queryForBatch(String dbEngineCode,
                                             UdfRegistrar udfRegistrar,
                                             TableMetaDataContainer tableMetaDataContainer,
                                             List<String> querySqlList) {

        if (tableMetaDataContainer == null
                || CollectionUtils.isEmpty(tableMetaDataContainer.getRegisteredTableMetaDataList())) {
            return null;
        }

        List<TableMetaData> tableMetaDataList = tableMetaDataContainer.getRegisteredTableMetaDataList();

        return this.queryForBatch(dbEngineCode, udfRegistrar, tableMetaDataList, querySqlList);
    }
}
