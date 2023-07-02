package com.howdev.framework.async.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * HttpEnabledHandler class
 *
 * @author haozhifeng
 * @date 2023/07/02
 */
@Aspect
@Component
@Order
public class HttpEnabledHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(HttpEnabledHandler.class);

    @Value("${async.sdk.http-manager.enabled:true}")
    private boolean httpManagerEnabled;

    @Pointcut("@annotation(HttpEnabled)")
    public void pointcut() {
        // nothing to do
    }

    @Around("pointcut()")
    public Object process(ProceedingJoinPoint pjp) throws Throwable {
        if (httpManagerEnabled) {
            return pjp.proceed();
        }

        LOGGER.info("async.sdk.http-manager.enabled=false, skip http api.");
        return null;
    }
}
