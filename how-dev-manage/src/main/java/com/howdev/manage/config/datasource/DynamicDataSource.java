package com.howdev.manage.config.datasource;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

import javax.sql.DataSource;
import java.util.Map;

/**
 * DynamicDataSource class
 * 定义动态数据源，继承扩展 Spring 的 AbstractRoutingDataSource 抽象类，实现动态切换数据源，
 * bstractRoutingDataSource 中的抽象方法 determineCurrentLookupKey 是实现数据源的route的核心。
 *
 * @author haozhifeng
 * @date 2023/02/10
 */
public class DynamicDataSource extends AbstractRoutingDataSource {
    /**
     * 决定使用哪个数据源之前需要把多个数据源的信息以及默认数据源信息配置好
     *
     * @param defaultTargetDataSource 默认数据源
     * @param targetDataSources       目标数据源
     * @return:
     * @author: haozhifeng
     */
    public DynamicDataSource(DataSource defaultTargetDataSource, Map<Object, Object> targetDataSources) {
        super.setDefaultTargetDataSource(defaultTargetDataSource);
        super.setTargetDataSources(targetDataSources);
        super.afterPropertiesSet();
    }

    @Override
    protected Object determineCurrentLookupKey() {
        // 使用DynamicDataSourceHolder保证线程安全，并且得到当前线程中的数据源key

        logger.info("DynamicDataSource getSourceKey: " + DynamicDataSourceHolder.getDataSourceKey());
        return DynamicDataSourceHolder.getDataSourceKey();
    }
}
