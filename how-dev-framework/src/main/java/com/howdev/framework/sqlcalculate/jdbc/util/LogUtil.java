package com.howdev.framework.sqlcalculate.jdbc.util;

import org.apache.commons.lang.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

/**
 * LogUtil class
 *
 * @author haozhifeng
 * @date 2023/12/12
 */
public class LogUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(LogUtil.class);


    public static String generateLogid(String prefix) {
        return prefix + RandomStringUtils.randomNumeric(20);
    }

    public static void bindLogId(String logId) {
        MDC.put("logid", logId);
    }


    public static String getLogId() {
        return MDC.get("logid");
    }

    public static void unbindLogId() {
        MDC.remove("logid");
    }


}
