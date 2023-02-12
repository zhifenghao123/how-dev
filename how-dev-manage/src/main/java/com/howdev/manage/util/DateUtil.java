package com.howdev.manage.util;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * DateUtil class
 *
 * @author haozhifeng
 * @date 2023/02/12
 */
public class DateUtil extends DateUtils {

    /** yyyyMM */
    public static final String MONTH_FORMAT = "yyyyMM";

    /** yyyyMMdd */
    public static final String SHORT_FORMAT = "yyyyMMdd";

    /** yyyyMMddHHmmss */
    public static final String LONG_FORMAT = "yyyyMMddHHmmss";

    /** yyyyMMddHHmmssSSS */
    public static final String LONG_FORMAT_MILLIS = "yyyyMMddHHmmssSSS";

    /** yyyy-MM-dd */
    public static final String WEB_FORMAT = "yyyy-MM-dd";

    /** yyyy-MM-dd HH:mm:ss */
    public static final String LONG_WEB_FORMAT = "yyyy-MM-dd HH:mm:ss";

    /** yyyy-MM-dd HH:mm:ss */
    public static final String DEFAULT_LONG_WEB_FORMAT = "1970-01-01 00:00:00";

    /**
     * 日期对象解析成日期字符串基础方法，可以据此封装出多种便捷的方法直接使用
     *
     * @param date 待格式化的日期对象
     * @param format 输出的格式
     * @return 格式化的字符串
     */
    public static String format(Date date, String format) {
        if (date == null || StringUtils.isBlank(format)) {
            return StringUtils.EMPTY;
        }

        return new SimpleDateFormat(format, Locale.SIMPLIFIED_CHINESE).format(date);
    }

}
