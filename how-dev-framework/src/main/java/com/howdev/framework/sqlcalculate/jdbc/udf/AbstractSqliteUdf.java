package com.howdev.framework.sqlcalculate.jdbc.udf;

import com.howdev.framework.sqlcalculate.jdbc.enumeration.DbEngineEnum;
import org.sqlite.Function;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

/**
 * AbstractSqliteUdf class
 *
 * @author haozhifeng
 * @date 2023/12/11
 */
public abstract class AbstractSqliteUdf extends Function implements Udf {
    @Override
    public void registerUdfBySelf(Connection connection, String functionName) throws SQLException {
        Function.create(connection, functionName, this);
    }

    @Override
    public List<DbEngineEnum> getSupportedDbEngines() {
        return Arrays.asList(DbEngineEnum.SQLITE);
    }
}
