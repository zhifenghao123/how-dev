package com.howdev.manage.exception;

import com.howdev.framework.log.core.LogMessageContainer;
import com.howdev.manage.enumeration.RetCodeEnum;

/**
 * RetCodeException class
 *
 * @author haozhifeng
 * @date 2023/12/07
 */
public class RetCodeException extends RuntimeException {

    /**
     * Bound error code
     */
    public final RetCodeEnum retCode;
    /**
     * Bound error message arguments
     */
    public final Object[] args;

    /**
     * Constructor with error code & args
     */
    public RetCodeException(RetCodeEnum retCode, Object... args) {
        this.retCode = retCode;
        this.args = args;
    }

    /**
     * Constructor with RetCodeEnum & args & Throwable
     */
    public RetCodeException(RetCodeEnum retCodeEnum, Throwable cause, Object... args) {
        super(cause);
        this.retCode = retCodeEnum;
        this.args = args;
    }

    @Override
    public String getMessage() {
        return LogMessageContainer.getFormattedMessage(retCode, args);
    }

    public RetCodeEnum getRetCode() {
        return this.retCode;
    }
}
