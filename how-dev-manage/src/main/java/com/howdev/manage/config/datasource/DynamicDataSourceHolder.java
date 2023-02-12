package com.howdev.manage.config.datasource;

/**
 * DynamicDataSourceHolder class
 *
 * @author haozhifeng
 * @date 2023/02/10
 */
public class DynamicDataSourceHolder {
    /**
     * 业务线A
     */
    public static final String BUSINESS_LINE_A = "A";

    /**
     * 业务线B
     */
    public static final String BUSINESS_LINE_B = "B";

    /**
     * 使用ThreadLocal记录当前线程的数据源key
     */
    private static final ThreadLocal<String> CONTEXT_HOLDER = new ThreadLocal<String>();

    /**
     * 设置数据源key
     * @param key
     */
    public static void setDataSourceKey(String key) {
        CONTEXT_HOLDER.set(key);
    }

    /**
     * 获取数据源key
     *
     * @param
     * @return:
     * @author: haozhifeng
     */
    public static String getDataSourceKey() {
        return CONTEXT_HOLDER.get();
    }

    /**
     * 清除数据源key
     *
     * @param
     * @return:
     * @author: haozhifeng
     */
    public static void clearDataSource() {
        CONTEXT_HOLDER.remove();
    }
}
