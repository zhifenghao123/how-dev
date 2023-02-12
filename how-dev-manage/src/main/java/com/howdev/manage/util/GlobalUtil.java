package com.howdev.manage.util;

import java.util.Date;

/**
 * GlobalUtil class
 *
 * @author haozhifeng
 * @date 2023/02/12
 */
public class GlobalUtil {
    private static final String LOCAL_IP = SystemUtil.getLocalIp();

    public GlobalUtil() {
    }

    public static String getSessionId() {
        return getGlobalId(DateUtil.format(new Date(), DateUtil.LONG_FORMAT) + "_");
    }

    public static String getGlobalId(String prefix) {
        return prefix + getGlobalId();
    }

    public static String getGlobalId() {
        long logId = getGlobalHash();
        return 0L > logId ? 'A' + String.valueOf(logId).substring(1) : String.valueOf(logId);
    }

    public static Long getGlobalHash() {
        long random = (long)(Math.random() * 9.223372036854776E18D);
        long time = System.nanoTime();
        return HashUtil.getHashCode(LOCAL_IP + time + random);
    }
}
