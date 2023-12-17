package com.howdev.framework.sqlcalculate.jdbc.enumeration;

/**
 * SqlCalculateErrorCode class
 *
 * @author haozhifeng
 * @date 2023/12/11
 */
public enum SqlCalculateErrorCode {
    /**
     * sql执行相关 E1开头
     */
    CREATE_MEMORY_DB_ERROR("E1001", "创建引擎类型为%s的内嵌数据库失败"),
    ENGINE_CODE_NOT_EXISTS("E1002", "%s对应的执行引擎不存在"),
    ENGINE_CLASS_NOT_EXISTS("E1003", "执行引擎%s对应的驱动类不存在"),
    CLOSE_EMBEDDED_DB_ERROR("E1004", "关闭sql引擎时发生异常"),
    EXE_INIT_SQL_ERROR("E1005", "执行查询前构建sql(元数据表sql、元数据sql、初始化sql)发生异常, sql语句为:[%s], 异常信息为:[%s]"),
    EXE_QUERY_SQL_ERROR("E1006", "执行查询sql时发生异常"),
    CLOSE_STATEMENT_ERROR("E1007", "关闭statement发生异常"),
    REGISTER_FUNCTION_ERROR("E1008", "注册自定义函数%s时发生异常"),
    SQL_RESULT_NOT_STRING("E1009", "sql执行返回的%s对应的值不是String类型, 具体为%s类型, 值为[%s], sql执行结果为: %s"),
    CANNOT_NUM_TO_STR("E1010", "执行num_to_str异常, 具体为值为%s类型, 值为[%s]"),


    ;

    private final String code;
    private final String msg;

    SqlCalculateErrorCode(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public String getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

    public String getFormatMsg(Object... args) {
        return String.format(msg, args);
    }

}
