package com.howdev.framework.sqlcalculate.jdbc.datasource;

import com.howdev.framework.sqlcalculate.jdbc.enumeration.DbEngineEnum;
import com.howdev.framework.sqlcalculate.jdbc.enumeration.SqlCalculateErrorCode;
import com.howdev.framework.sqlcalculate.jdbc.exception.SqlCalcException;
import com.howdev.framework.sqlcalculate.jdbc.udf.Udf;
import com.howdev.framework.sqlcalculate.jdbc.udf.UdfRegistrar;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;

/**
 * Helper class that provides static methods for obtaining JDBC Connections from a {@link javax.sql.DataSource}.
 *
 * @author haozhifeng
 * @date 2023/12/11
 */
public abstract class DataSourceUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(DataSourceUtils.class);

    /**
     * 创建Connection
     *
     * @param engineCode engineCode
     * @return:
     * @author: haozhifeng
     */
    public static Connection createConnection(String engineCode) {
        DbEngineEnum dbEngine = DbEngineEnum.findByEngineCode(engineCode);
        if (Objects.isNull(dbEngine)) {
            throw new SqlCalcException(SqlCalculateErrorCode.ENGINE_CODE_NOT_EXISTS, engineCode);
        }

        Connection connection;
        try {
            Class.forName(dbEngine.getJdbcDriverName());
            connection = DriverManager.getConnection(dbEngine.getConnectionUrl());
        } catch (ClassNotFoundException ex) {
            LOGGER.error("jdbc driver class not found", ex);
            throw new SqlCalcException(SqlCalculateErrorCode.ENGINE_CLASS_NOT_EXISTS, engineCode);
        } catch (SQLException e) {
            LOGGER.error("jdbc driver class load exception ", e);
            throw new SqlCalcException(SqlCalculateErrorCode.CREATE_MEMORY_DB_ERROR, engineCode);
        }

        if (connection == null) {
            throw new SqlCalcException(SqlCalculateErrorCode.CREATE_MEMORY_DB_ERROR, engineCode);
        }

        return connection;
    }

    /**
     * 注册自定义函数
     *
     * @param connection connection
     * @param udfRegistrar udfRegistrar
     * @return:
     * @author: haozhifeng
     */
    public static void registerUdf(Connection connection, UdfRegistrar udfRegistrar) {

        List<Udf> udfs = udfRegistrar.getUdfs();
        if (CollectionUtils.isEmpty(udfs)) {
            return;
        }

        for (Udf udf : udfs) {
            String udfName = udf.getUdfName();
            try {
                udf.registerUdfBySelf(connection, udfName);
            } catch (SQLException e) {
                LOGGER.error("注册自定义函数时发生异常, 函数为{}", udfName, e);
                throw new SqlCalcException(SqlCalculateErrorCode.REGISTER_FUNCTION_ERROR, udfName);
            }
        }
    }

    /**
     * closeConnection
     * 
     * @param connection connection
     * @return: 
     * @author: haozhifeng     
     */
    public static void closeConnection(Connection connection) {
        try {
            if (connection != null) {
                connection.close();
                LOGGER.info("[closeConnection] close embedded db success");
            }
        } catch (SQLException e) {
            LOGGER.info("链接关闭失败", e);
            throw new SqlCalcException(SqlCalculateErrorCode.CLOSE_EMBEDDED_DB_ERROR);
        }
    }
}
