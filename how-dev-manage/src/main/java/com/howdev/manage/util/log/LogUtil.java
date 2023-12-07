package com.howdev.manage.util.log;

import com.howdev.manage.util.GlobalUtil;
import org.slf4j.MDC;

/**
 * LogUtil class
 *
 * @author haozhifeng
 * @date 2023/02/12
 */
public class LogUtil {

    private LogUtil() {
        // no need to do.
    }

    public static void buildAndBindLog() {
        bindLogId(GlobalUtil.getGlobalId());
    }

    public static void bindLogId(String logId) {
        MDC.put("logid", logId);
    }

    public static void unbindLogId() {
        MDC.remove("logid");
    }
}
