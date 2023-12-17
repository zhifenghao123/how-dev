package com.howdev.framework.sqlcalculate.jdbc.enumeration;

/**
 * DbEngineEnum class
 *
 * @author haozhifeng
 * @date 2023/12/11
 */
public enum DbEngineEnum {
    /**
     * h2PostgreSQL：使用H2实现的内存数据库, 语法基本兼容PostgreSQL, 即语法类似于gp
     * 创建内存h2数据库 且数据库值忽略大小写 但是字段名不忽略大小写
     */
    H2_POSTGRESQL("h2PostgreSQL", "org.h2.Driver", "jdbc:h2:mem:;DATABASE_TO_UPPER=false;mode=PostgreSQL;"),
    /**
     * h2MySql：使用H2实现的内存数据库, 语法基本兼容mysql
     * 创建内存h2数据库 且数据库值忽略大小写 但是字段名不忽略大小写
     */
    H2_MYSQL("h2MySql", "org.h2.Driver", "jdbc:h2:mem:;DATABASE_TO_UPPER=false;mode=MySQL;"),

    /**
     * sqlite：Sqlite引擎, 语法类似于mysql
     *  创建内存Sqlite数据库 且忽略大小写
     */
    SQLITE("sqlite", "org.sqlite.JDBC", "jdbc:sqlite::memory:");


    /**
     * 数据库引擎码值
     */
    private final String engineCode;
    /**
     * 驱动类名
     */
    private final String jdbcDriverName;
    /**
     * connectionUrl
     */
    private final String connectionUrl;

    DbEngineEnum(String engineCode, String jdbcDriverName, String connectionUrl) {
        this.engineCode = engineCode;
        this.jdbcDriverName = jdbcDriverName;
        this.connectionUrl = connectionUrl;
    }

    public String getEngineCode() {
        return engineCode;
    }

    public String getJdbcDriverName() {
        return jdbcDriverName;
    }

    public String getConnectionUrl() {
        return connectionUrl;
    }

    /**
     * findByEngineCode
     *
     * @param engineCode engineCode
     * @return:
     * @author: haozhifeng
     */
    public static DbEngineEnum findByEngineCode(String engineCode) {
        for (DbEngineEnum dbEngine : values()) {
            if (dbEngine.engineCode.equals(engineCode)) {
                return dbEngine;
            }
        }
        return null;
    }
}
