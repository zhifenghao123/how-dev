package com.howdev.common.exception;

/**
 * JacksonException class
 *
 * @author haozhifeng
 * @date 2023/03/21
 */
public class JacksonException extends RuntimeException {
    public JacksonException() {
        // no need to do.
    }

    public JacksonException(String message) {
        super(message);
    }
}
