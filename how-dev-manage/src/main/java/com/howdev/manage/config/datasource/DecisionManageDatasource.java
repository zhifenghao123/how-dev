package com.howdev.manage.config.datasource;

import com.howdev.manage.annotation.SwitchDataSourceTag;
import com.zaxxer.hikari.HikariDataSource;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import javax.sql.DataSource;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * DecisionManageDatasource class
 *
 * @author haozhifeng
 * @date 2023/02/10
 */
@Configuration
@MapperScan(basePackages = "com.howdev.manage.mapper",
        sqlSessionFactoryRef = "decisionManageSqlSessionFactory")
public class DecisionManageDatasource {
    /**
     * 访问A业务线数据库集群的数据源
     *
     * @param
     * @return:
     * @author: haozhifeng
     */
    @Bean(name = "aDecisionManageDataSource")
    @ConfigurationProperties("spring.datasource.decision.manage.a")
    public DataSource aDecisionManageDataSource() {
        return new HikariDataSource();
    }

    /**
     * 访问B业务线数据库集群的数据源
     *
     * @param
     * @return:z
     * @author: haozhifeng
     */
    @Bean(name = "bDecisionManageDataSource")
    @ConfigurationProperties("spring.datasource.decision.manage.b")
    public DataSource bDecisionManageDataSource() {
        return new HikariDataSource();
    }
    

    /**
     * 动态数据源
     *
     * @param aDecisionManageDataSource 访问A业务线数据库集群的数据源
     * @param bDecisionManageDataSource 访问B业务线数据库集群的数据源
     * @return:
     * @author: haozhifeng
     */
    @Bean
    public DynamicDataSource dynamicDecisionManageDataSource(
            @Qualifier("aDecisionManageDataSource") DataSource aDecisionManageDataSource,
            @Qualifier("bDecisionManageDataSource") DataSource bDecisionManageDataSource) {
        Map<Object, Object> targetDataSources = new HashMap<>();
        targetDataSources.put(
                StringUtils.join(Arrays.asList(DynamicDataSourceHolder.BUSINESS_LINE_A,
                        SwitchDataSourceTag.DATABASE_DECISION_MANAGE), "_"), aDecisionManageDataSource);
        targetDataSources.put(
                StringUtils.join(Arrays.asList(DynamicDataSourceHolder.BUSINESS_LINE_B,
                        SwitchDataSourceTag.DATABASE_DECISION_MANAGE), "_"), bDecisionManageDataSource);
        return new DynamicDataSource(aDecisionManageDataSource, targetDataSources);
    }

    /**
     * 数据源事务管理器
     *
     * @param aDecisionManageDataSource 访问A业务线数据库集群的数据源
     * @param bDecisionManageDataSource 访问B业务线数据库集群的数据源
     * @return DataSourceTransactionManager
     */
    @Bean(name = "decisionManageDataSourceTransactionManager")
    public DataSourceTransactionManager DecisionManageDataSourceTransactionManager(
            @Qualifier("aDecisionManageDataSource") DataSource aDecisionManageDataSource,
            @Qualifier("bDecisionManageDataSource") DataSource bDecisionManageDataSource) {
        return new DataSourceTransactionManager(dynamicDecisionManageDataSource(aDecisionManageDataSource,
                bDecisionManageDataSource));
    }

    /**
     * 创建Session
     *
     * @param dataSource 数据源.
     * @return SqlSessionFactory
     */
    @Bean(name = "decisionManageSqlSessionFactory")
    public SqlSessionFactory decisionManageSqlSessionFactory(
            @Qualifier("dynamicDecisionManageDataSource") DataSource dataSource) throws Exception {
        final SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
        sqlSessionFactoryBean.setDataSource(dataSource);

        // 设置xml
        sqlSessionFactoryBean.setMapperLocations(
                new PathMatchingResourcePatternResolver().getResources("classpath:sql/*.xml"));

        return sqlSessionFactoryBean.getObject();
    }

}
