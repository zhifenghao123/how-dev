package com.howdev.framework.sqlcalculate.jdbc.sqlservice;

import com.howdev.framework.sqlcalculate.jdbc.cacheloader.SqlServiceConfigCacheLoader;
import com.howdev.framework.sqlcalculate.jdbc.core.SqlCalcJdbcTemplate;
import com.howdev.framework.sqlcalculate.jdbc.core.TableMetaDataContainer;
import com.howdev.framework.sqlcalculate.jdbc.enumeration.DbEngineEnum;
import com.howdev.framework.sqlcalculate.jdbc.udf.UdfRegistrar;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

/**
 * SqlCalculate class
 *
 * @author haozhifeng
 * @date 2023/12/13
 */
public abstract class SqlCalculate {
    Logger LOGGER = LoggerFactory.getLogger(SqlCalculate.class);

    /**
     * sqlServiceKey
     * @return
     */
    protected abstract String getSqlServiceKey();
    /**
     * 使用的数据库引擎
     * @return
     */
    protected abstract DbEngineEnum getUsedDbEngine();

    /**
     * UdfRegistrar
     * @return
     */
    protected abstract UdfRegistrar getUdfRegistrar();


    List<String> getQuerySqlList(){
        String sqlServiceKey = getSqlServiceKey();
        return SqlServiceConfigCacheLoader.loadSqlVarConfig(sqlServiceKey);
    }

    Map<String, Object> doSqlCalculate(DbEngineEnum usedDbEngine, UdfRegistrar udfRegistrar,
                                       TableMetaDataContainer tableMetaDataContainer, List<String> querySqlList) {

        SqlCalcJdbcTemplate sqlCalc = new SqlCalcJdbcTemplate();

        Map<String, Object> sqlCalcResult =
                sqlCalc.queryForBatch(usedDbEngine.getEngineCode(), udfRegistrar, tableMetaDataContainer, querySqlList);

        return sqlCalcResult;
    }

    Map<String, Object> doSqlCalculate(TableMetaDataContainer tableMetaDataContainer, List<String> querySqlList) {

        DbEngineEnum usedDbEngine = getUsedDbEngine();

        UdfRegistrar udfRegistrar = getUdfRegistrar();

        Map<String, Object> sqlCalcResult = doSqlCalculate(usedDbEngine, udfRegistrar, tableMetaDataContainer, querySqlList);

        return sqlCalcResult;
    }

    protected Map<String, Object> doSqlCalculate(TableMetaDataContainer tableMetaDataContainer) {

        DbEngineEnum usedDbEngine = getUsedDbEngine();

        UdfRegistrar udfRegistrar = getUdfRegistrar();

        List<String> querySqlList = getQuerySqlList();

        Map<String, Object> sqlCalcResult = doSqlCalculate(usedDbEngine, udfRegistrar, tableMetaDataContainer, querySqlList);

        return sqlCalcResult;
    }
}
