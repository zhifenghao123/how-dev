package com.howdev.manage.aspect;

import com.howdev.manage.annotation.SwitchDataSourceTag;
import com.howdev.manage.config.datasource.DynamicDataSourceHolder;
import com.howdev.manage.util.ObjectFieldUtil;
import com.howdev.manage.util.SpelUtil;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Map;

/**
 * SwitchDataSourceAspect class
 *
 * @author haozhifeng
 * @date 2023/12/08
 */
@Aspect
@Order(1)
@Component
public class SwitchDataSourceAspect {
    private static final Logger LOGGER = LoggerFactory.getLogger(SwitchDataSourceAspect.class);

    public static final String PARAMS_REQUEST = "request";
    public static final String PARAMS_REQUEST_BIZ_LINE = "bizLine";

    private SwitchDataSourceAspect() {
        // no need to do
    }

    @Pointcut("execution(@com.howdev.manage.annotation.SwitchDataSourceTag * *(..))")
    public void pointcut() {
        // nothing to do
    }

    @Around("pointcut()")
    public Object process(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        MethodSignature methodSignature = (MethodSignature) proceedingJoinPoint.getSignature();
        Map<String, Object> requestParamMap;
        String bizLine;

        // 设置执行结果
        Object result = null;

        requestParamMap = SpelUtil.getParamMap(proceedingJoinPoint, methodSignature);
        SwitchDataSourceTag switchDataSourceTag = methodSignature.getMethod().getAnnotation(SwitchDataSourceTag.class);
        // 方法上有@SwitchDataSourceTag注解，则说明需要动态切换数据源
        if (switchDataSourceTag != null) {
            Object paramObject = MapUtils.getObject(requestParamMap, PARAMS_REQUEST);
            bizLine = null != paramObject ? (String) ObjectFieldUtil.getFieldValueWithException(paramObject,
                    PARAMS_REQUEST_BIZ_LINE) : null;
            LOGGER.info("bizLine:" + bizLine);

            String operateDatabase = switchDataSourceTag.operateDatabase();

            DynamicDataSourceHolder.setDataSourceKey(
                    StringUtils.join(Arrays.asList(bizLine, operateDatabase), "_"));

            LOGGER.info("AOP  setDataSourceKey:" + DynamicDataSourceHolder.getDataSourceKey());

        }

        result = proceedingJoinPoint.proceed();

        return result;
    }
}
