package com.howdev.framework.sqlcalculate.example.udf.sqlite;

import com.howdev.framework.sqlcalculate.jdbc.enumeration.SqlCalculateErrorCode;
import com.howdev.framework.sqlcalculate.jdbc.exception.SqlCalcException;
import com.howdev.framework.sqlcalculate.jdbc.udf.AbstractSqliteUdf;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sqlite.core.Codes;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.SQLException;

/**
 * SqliteNumToStrFunction class
 * 输入数字, 返回四舍五入保留2位小数点的字符串,如num_to_str(13.1376)返回13.14
 * 使用示例：num_to_str(13.1376)
 *
 * @author haozhifeng
 * @date 2023/12/11
 */
public class SqliteNumToStrFunction extends AbstractSqliteUdf {
    private static final Logger LOGGER = LoggerFactory.getLogger(SqliteNumToStrFunction.class);

    private static final String UDF_NAME = "num_to_str";

    private static final int FIRST_ARG_INDEX = 0;

    @Override
    public String getUdfName() {
        return UDF_NAME;
    }

    @Override
    protected void xFunc() throws SQLException {
        int valueType = value_type(FIRST_ARG_INDEX);
        // 如果是null 返回 null
        if (valueType == Codes.SQLITE_NULL) {
            result((String) null);
        }
        // 如果是double 返回数值
        else if (valueType == Codes.SQLITE_INTEGER || valueType == Codes.SQLITE_FLOAT) {
            double originResult = value_double(FIRST_ARG_INDEX);
            String result = numTo2Point(originResult);
            result(result);
        }
        // 如果是str 尝试转换
        else {
            String value = value_text(FIRST_ARG_INDEX);
            String result = numStrTo2Point(valueType, value);
            result(result);
        }
    }

    /**
     * 数值字符串保留2位后返回
     */
    String numStrTo2Point(int valueType, String value) {
        try {
            BigDecimal decimalNum = new BigDecimal(value);
            return decimalNum.setScale(2, RoundingMode.HALF_UP).toPlainString();
        } catch (NumberFormatException e) {
            LOGGER.error("sql计算结果num_to_str异常, valueType={}, value={}, errorcode = {}",
                    valueType, value, SqlCalculateErrorCode.CANNOT_NUM_TO_STR.getCode(), e);
            throw new SqlCalcException(SqlCalculateErrorCode.CANNOT_NUM_TO_STR, valueType, value);
        }
    }

    /**
     * double保留2位后返回
     */
    String numTo2Point(double value) {
        BigDecimal decimalNum = new BigDecimal(value).setScale(2, RoundingMode.HALF_UP);
        return decimalNum.toPlainString();
    }

}
