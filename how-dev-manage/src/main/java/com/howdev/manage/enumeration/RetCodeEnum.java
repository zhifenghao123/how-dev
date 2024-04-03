package com.howdev.manage.enumeration;

import com.howdev.common.exceptions.ErrorCodeEnum;

import java.text.MessageFormat;

/**
 * RetCodeEnum class
 *
 * @author haozhifeng
 * @date 2023/02/12
 */
public enum  RetCodeEnum implements ErrorCodeEnum {
    /**
     * 成功
     */
    SUCCESS("0", "OK"),

    /**
     * 参数不合法
     */
    ILLEGAL_ARGUMENT("E1000", "参数不合法: {0}"),
    /**
     * 处理失败
     */
    FAILED("F1000", "处理失败: {0}");

    /**
     * 返回码
     */
    private final String retCode;
    /**
     * 返回消息
     */
    private final String retMsg;

    RetCodeEnum(String retCode, String retMsg) {
        this.retCode = retCode;
        this.retMsg = retMsg;
    }

    public static boolean isSuccess(String retCode) {
        return RetCodeEnum.SUCCESS.retCode.equals(retCode);
    }

    public String getRetCode() {
        return retCode;
    }

    public String getRetMsg() {
        return retMsg;
    }

    @Override
    public String getErrorCode() {
        return getRetCode();
    }

    @Override
    public String getErrorMessage() {
        return getRetMsg();
    }
}
