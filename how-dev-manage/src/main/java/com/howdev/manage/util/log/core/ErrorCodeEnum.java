package com.howdev.manage.util.log.core;

/**
 * ErrorCodeEnum class
 *
 * @author haozhifeng
 * @date 2023/12/07
 */
public interface ErrorCodeEnum {
    /**
     * 获取错误码
     *
     * @param
     * @return: 错误码字符串
     * @author: haozhifeng
     */
    String getErrorCode();

    /**
     * 获取错误码对应的错误消息，占位符可以用 {0}/{1} 替代，在 FormattedLogMessage 中可以根据参数替换占位符，也可以不使用占位符
     *
     * @param
     * @return:  错误消息字符串
     * @author: haozhifeng
     */
    String getErrorMessage();
}
