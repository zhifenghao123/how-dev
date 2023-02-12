package com.howdev.manage.util;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.util.HashMap;
import java.util.Map;

/**
 * spel工具类
 *
 * @author haozhifeng
 * @date 2023/02/10
 */
public class SpelUtil {

    private SpelUtil() {

    }

    /**
     * @param spel spel 表达式
     * @param proceedingJoinPoint 切入点
     * @param clazz 返回类型
     */
    public static <T> T getValue(String spel, ProceedingJoinPoint proceedingJoinPoint,
                                 Class<T> clazz) {
        MethodSignature signature = (MethodSignature) proceedingJoinPoint.getSignature();

        Map<String, Object> vars = getParamMap(proceedingJoinPoint, signature);
        ExpressionParser expressionParser = new SpelExpressionParser();
        StandardEvaluationContext ctx = new StandardEvaluationContext();
        ctx.setVariables(vars);
        return expressionParser.parseExpression(spel).getValue(ctx, clazz);
    }

    public static Map<String, Object> getParamMap(ProceedingJoinPoint proceedingJoinPoint,
                                                  MethodSignature signature) {
        Map<String, Object> vars = new HashMap<>(signature.getParameterNames().length << 1);
        for (int i = 0; i < signature.getParameterNames().length; i++) {
            vars.put(signature.getParameterNames()[i], proceedingJoinPoint.getArgs()[i]);
        }
        return vars;
    }
}
