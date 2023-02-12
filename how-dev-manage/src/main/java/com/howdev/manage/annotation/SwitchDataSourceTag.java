package com.howdev.manage.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * SwitchDataSourceTag class
 *
 * @author haozhifeng
 * @date 2023/02/10
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface SwitchDataSourceTag {
    /**
     * 对流程平台运行时数据库库表进行读或者写
     */
    public static final String DATABASE_DECISION_MANAGE = "decisionManage";

    String operateDatabase() default "";
}
