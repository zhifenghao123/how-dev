package com.howdev.manage.advice;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * ExceptionAdvice class
 *
 * @author haozhifeng
 * @date 2023/12/07
 */
@ControllerAdvice
public class ExceptionAdvice {
    private static final Logger LOGGER = LoggerFactory.getLogger(ExceptionAdvice.class);

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleIOException(Exception ex) {
        LOGGER.error("[全局异常捕获] ex: ", ex);
        String errMsg = ex.getMessage();
        // 处理异常并返回错误码和错误消息
        // 使用自定义错误码或默认错误码500
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error occurred while downloading the file: " + errMsg);
    }
}
