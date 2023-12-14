package com.howdev.framework.sqlcalculate.jdbc.table;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * EntityTableConvertible class
 *
 * @author haozhifeng
 * @date 2023/12/13
 */
public interface EntityTableConvertible {
    /**
     * 转换为insert sql
     * @return
     * @author haozhifeng
     */
    String convertToInsertSql();

    default String convertToInsertVarcharValue(Object obj) {
        if (obj == null) {
            return null;
        }else if (obj instanceof String) {
            return "'" + obj + "'";
        } else if (obj instanceof Date) {
            return "'" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(obj) + "'";
        } else if (obj instanceof Boolean) {
            if ((Boolean) obj) {
                return "1";
            } else {
                return "0";
            }
        } else {
            return obj.toString();
        }
    }
}
