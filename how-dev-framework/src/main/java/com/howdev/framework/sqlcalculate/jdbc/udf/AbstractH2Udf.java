package com.howdev.framework.sqlcalculate.jdbc.udf;

import com.howdev.framework.sqlcalculate.jdbc.enumeration.DbEngineEnum;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.List;

/**
 * AbstractH2Udf class
 *
 * @author haozhifeng
 * @date 2023/12/11
 */
public abstract class AbstractH2Udf implements Udf {
    private static final String REGISTRAR_UDF_STATEMENT = "CREATE ALIAS IF NOT EXISTS \"%s\" FOR \"%s\";";

    @Override
    public void registerUdfBySelf(Connection connection, String functionName) throws SQLException {
        String classAndMethodName = getClassAndMethodName();
        try (Statement statement = connection.createStatement()) {
            statement.execute(String.format(REGISTRAR_UDF_STATEMENT, functionName, classAndMethodName));
        }
    }

    /**
     * h2自定义函数必须是public static的
     * 该方法返回格式类似com.howdev.sqlcalculate.jdbc.udf.Test.function
     */
    public abstract String getClassAndMethodName();

    @Override
    public List<DbEngineEnum> getSupportedDbEngines() {
        return Arrays.asList(DbEngineEnum.H2_MYSQL, DbEngineEnum.H2_POSTGRESQL);
    }
}
