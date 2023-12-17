package com.howdev.framework.sqlcalculate.jdbc.sqlservice;

import com.howdev.framework.sqlcalculate.jdbc.cacheloader.SqlServiceConfigCacheLoader;
import com.howdev.framework.sqlcalculate.jdbc.core.SqlCalcJdbcTemplate;
import com.howdev.framework.sqlcalculate.jdbc.core.TableMetaDataContainer;
import com.howdev.framework.sqlcalculate.jdbc.enumeration.DbEngineEnum;
import com.howdev.framework.sqlcalculate.jdbc.udf.UdfRegistrar;
import org.apache.commons.collections4.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

    /**
     * 执行sql前的后置处理
     * 默认只做了用户变量的替换；
     * 如果想扩展改动能，可以Override该方法；
     * 如果想在该功能的基础上扩展其他功能，可以Override该方法，并在Override的方法内调用该方法(super.postProcessSqlBeforeExecute)，然后扩展其他功能；
     * @param sql
     * @param userVariables
     * @return
     */
    String postProcessSqlBeforeExecute(String sql, Map<String, Object> userVariables){
        if (MapUtils.isEmpty(userVariables)){
            return sql;
        }

        // 替换用户变量
        String querySqlWithUserVarReplaced = sql;
        for (Map.Entry<String, Object> userVariable : userVariables.entrySet()) {
            String key = "@" + userVariable.getKey();
            Object value = userVariable.getValue();
            if (querySqlWithUserVarReplaced.contains(key)) {
                if (value instanceof String) {
                    querySqlWithUserVarReplaced = querySqlWithUserVarReplaced.replace(key, "'" + value + "'");
                } else {
                    querySqlWithUserVarReplaced = querySqlWithUserVarReplaced.replace(key, value.toString());
                }
            }
        }
        return querySqlWithUserVarReplaced;
    }

    private Map<String, Object> doCalculateSql(DbEngineEnum usedDbEngine, UdfRegistrar udfRegistrar,
                                     TableMetaDataContainer tableMetaDataContainer, Map<String, Object> userVariables,
                                     List<String> querySqlList) {

        SqlCalcJdbcTemplate sqlCalc = new SqlCalcJdbcTemplate();

        // 对sql进行预处理
        List<String> finalQuerySqlList = querySqlList.stream()
                .map(querySql -> postProcessSqlBeforeExecute(querySql, userVariables))
                .collect(Collectors.toList());

        // 执行sql
        Map<String, Object> sqlCalcResult =
                sqlCalc.queryForBatch(usedDbEngine.getEngineCode(), udfRegistrar, tableMetaDataContainer, finalQuerySqlList);

        return sqlCalcResult;
    }

    Map<String, Object> calculateSql(TableMetaDataContainer tableMetaDataContainer, Map<String, Object> userVariables,
                                     List<String> querySqlList) {

        DbEngineEnum usedDbEngine = getUsedDbEngine();

        UdfRegistrar udfRegistrar = getUdfRegistrar();

        Map<String, Object> sqlCalcResult = doCalculateSql(usedDbEngine, udfRegistrar, tableMetaDataContainer, userVariables, querySqlList);

        return sqlCalcResult;
    }

    protected Map<String, Object> calculateSql(TableMetaDataContainer tableMetaDataContainer, Map<String, Object> userVariables) {

        DbEngineEnum usedDbEngine = getUsedDbEngine();

        UdfRegistrar udfRegistrar = getUdfRegistrar();

        List<String> querySqlList = getQuerySqlList();

        Map<String, Object> sqlCalcResult = doCalculateSql(usedDbEngine, udfRegistrar, tableMetaDataContainer,userVariables, querySqlList);

        return sqlCalcResult;
    }
}
