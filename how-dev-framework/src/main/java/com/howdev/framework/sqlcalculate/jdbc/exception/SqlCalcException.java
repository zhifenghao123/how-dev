package com.howdev.framework.sqlcalculate.jdbc.exception;

import com.howdev.framework.sqlcalculate.jdbc.enumeration.SqlCalculateErrorCode;

/**
 * SqlCalcException class
 *
 * @author haozhifeng
 * @date 2023/12/11
 */
public class SqlCalcException extends RuntimeException {
    /**
     * 错误码
     */
    private final SqlCalculateErrorCode sqlCalculateErrorCode;

    public SqlCalcException(SqlCalculateErrorCode sqlCalculateErrorCode, Object... args) {
        super(sqlCalculateErrorCode.getFormatMsg(args));
        this.sqlCalculateErrorCode = sqlCalculateErrorCode;
    }
}
