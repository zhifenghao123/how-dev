package com.howdev.manage.aspect;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.howdev.common.exceptions.LogMessageContainer;
import com.howdev.manage.enumeration.RetCodeEnum;
import com.howdev.manage.exception.RetCodeException;
import com.howdev.manage.util.JacksonUtil;
import com.howdev.manage.util.SpelUtil;
import com.howdev.manage.util.SystemUtil;
import com.howdev.manage.util.log.LogUtil;
import com.howdev.manage.util.log.LoggerProxy;
import com.howdev.manage.vo.BaseResponse;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.core.BridgeMethodResolver;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.ClassUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * ExceptionAspect class
 *
 * @author haozhifeng
 * @date 2023/02/10
 */
@Aspect
@Order(0)
@Component
public class ExceptionAspect {
    private static final Logger LOGGER = LoggerFactory.getLogger(ExceptionAspect.class);

    private static int MAX_RETRY_TIME = 3;


    private ExceptionAspect() {
        // no need to do
    }

//    @Pointcut("execution(@com.howdev.manage.annotation.ApiException com.howdev.manage.vo.BaseResponse "
//            + "*(..))"
//            + "|| within(@com.howdev.manage.annotation.ApiException *)")


    /**
     * 切点：所有含@ApiException注解的方法
     * 或者 有@ApiException注解的类中所有返回BaseResponse对象的方法
     *
     */
    @Pointcut("execution(@com.howdev.manage.annotation.ApiException com.howdev.manage.vo.BaseResponse *(..)) " +
            "|| (execution(com.howdev.manage.vo.BaseResponse *(..)) && within(@com.howdev.manage.annotation.ApiException *)))")
    public void pointcut() {
        // nothing to do
    }

    @Around("pointcut()")
    public Object process(ProceedingJoinPoint proceedingJoinPoint) {
        // 设置重试次数, 记录开始时间
        long start = System.currentTimeMillis();
        int retryTime = 0;

        LogUtil.buildAndBindLog();

        // 设置执行结果
        Object result = null;

        // 获取方法信息
        Object[] args = proceedingJoinPoint.getArgs();
        Method method = findSpecificMethod(proceedingJoinPoint);
        MethodSignature methodSignature = (MethodSignature) proceedingJoinPoint.getSignature();

        // request，获取ip/url等
        HttpServletRequest request = getHttpServletRequest();
        String sourceIp = getIpAddress(request);
        String url = getUrl(methodSignature, request);
        ObjectWriter writer = JacksonUtil.getWriter();
        Map<String, Object> requestParamMap = null;
        String requestArgs = null;
        String bizLine = null;

        try {
            requestParamMap = SpelUtil.getParamMap(proceedingJoinPoint, methodSignature);
            requestArgs = writer.writeValueAsString(requestParamMap);
        } catch (JsonProcessingException e) {
            LOGGER.error("parse json error", e);
        }

        Logger logger = getLogger(proceedingJoinPoint);

        LoggerProxy.LoggerTemplate.RecievedMessage_HTTP.log(logger, url,
                sourceIp, SystemUtil.getLocalIp(), requestArgs);

        while (retryTime < MAX_RETRY_TIME && result == null) {

            try {

                result = proceedingJoinPoint.proceed();
            } catch (ConstraintViolationException e) {
                // 处理参数校验异常异常
                handleConstraintViolationException(e, args, methodSignature);
            } catch (IllegalArgumentException e){
                // 处理 IllegalArgumentException 异常
                result = buildErrorResponse(methodSignature, RetCodeEnum.ILLEGAL_ARGUMENT, e.getMessage());
            }  catch (RetCodeException e) {
                // 处理 RetCodeException 异常
                LOGGER.error("[全局异常捕获] code: {}", e.getRetCode(), e);
                result = buildErrorResponse(methodSignature, e.getRetCode(), e.getMessage());
            } catch (Throwable e) {
                // TODO LOG
                LOGGER.error("build error response fail.", e);
            }

            retryTime = retryTime + 1;
        }

        // 超出重试次数依然无法获得结果, 设置未知错误的返回
        if (!methodSignature.getReturnType().equals(Void.TYPE) && result == null) {
            result = buildErrorResponse(methodSignature, RetCodeEnum.FAILED, "Unknown error!");
        }

        LoggerProxy.LoggerTemplate.processMessageFinished_HTTP.log(logger, url,
                sourceIp, SystemUtil.getLocalIp(), requestArgs, JacksonUtil.toJson(result),
                RetCodeEnum.SUCCESS.getRetCode(), System.currentTimeMillis() - start);

        LogUtil.unbindLogId();

        return result;
    }

    private Logger getLogger(ProceedingJoinPoint proceedingJoinPoint) {
        MethodSignature signature = (MethodSignature) proceedingJoinPoint.getSignature();
        return LoggerFactory.getLogger(signature.getDeclaringType());
    }

    /**
     * 处理参数校验异常
     *
     * @param e               ConstraintViolationException
     * @param args            方法参数
     * @param methodSignature 方法签名
     * @return 根据异常生成返回结果
     */
    private Object handleConstraintViolationException(ConstraintViolationException e, Object[] args,
                                                      MethodSignature methodSignature) {
        // 错误码固定位参数不合法
        RetCodeEnum illegalArgRetCode = RetCodeEnum.ILLEGAL_ARGUMENT;

        // 组装异常内的错误信息
        String errorMsgFromException = e.getConstraintViolations().stream().map(ConstraintViolation::getMessage)
                .collect(Collectors.joining(" "));

        // 生成错误码对应的错误信息
        String errorMessage = LogMessageContainer.getFormattedMessage(illegalArgRetCode, errorMsgFromException);

        // TODO LOG.

        // 组装返回结果
        return buildErrorResponse(methodSignature, RetCodeEnum.ILLEGAL_ARGUMENT, errorMessage);
    }

    /**
     * 根据错误信息构造 response
     *
     * @param methodSignature 方法签名
     * @param retCode         错误码
     * @param errorMsg        错误消息
     * @return 错误码对应的返回结果
     */
    @SuppressWarnings("unchecked")
    private Object buildErrorResponse(MethodSignature methodSignature, RetCodeEnum retCode, String errorMsg) {
        try {
            Constructor<? extends BaseResponse> constructor =
                    methodSignature.getReturnType().getConstructor(String.class, String.class);
            // construct default response
            return constructor.newInstance(retCode.getRetCode(), errorMsg);
        } catch (Exception e) {
            LOGGER.error("build error response fail. retCode={}, errorMsg={}", retCode, errorMsg, e);
            return null;
        }
    }

    /**
     * 根据 ProceedingJoinPoint 找到最具体的执行方法（穿透代理等）
     *
     * @param pjp 切面
     * @return 切面执行的方法 {@link Method}
     */
    private Method findSpecificMethod(ProceedingJoinPoint pjp) {

        // 当前正在执行的方法
        MethodSignature methodSign = (MethodSignature) pjp.getStaticPart().getSignature();
        Method method = methodSign.getMethod();

        // 找到代理对象的真实执行方法
        Method specificMethod = ClassUtils.getMostSpecificMethod(
                method, AopProxyUtils.ultimateTargetClass(pjp.getThis())
        );

        // 如果要处理带有泛型参数的方法，找到原始方法
        return BridgeMethodResolver.findBridgedMethod(specificMethod);
    }

    public static HttpServletRequest getHttpServletRequest() {
        return ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
    }

    /**
     * 获取 ip 地址
     *
     * @param request HttpServletRequest http请求参数
     * @return 真实的 ip 地址
     */
    private String getIpAddress(HttpServletRequest request) {

        try {
            String ip = request.getHeader("X-Real-IP");
            if (StringUtils.isNotBlank(ip) && !"unknow".equalsIgnoreCase(ip)) {
                return ip;
            }

            ip = request.getHeader("X-Forwarded-For");
            if (StringUtils.isNotBlank(ip) && !"unknow".equalsIgnoreCase(ip)) {
                // 多次反向代理后会有多个IP值，第一个为真实IP。
                int index = ip.indexOf(',');
                if (index == -1) {
                    return ip;
                } else {
                    return ip.substring(0, index);
                }
            }
            return request.getRemoteAddr();

        } catch (Exception e) {
            LOGGER.error("get remote ip fail.", e);
        }

        return "unknow";
    }

    /**
     * 获取真实的url
     *
     * @param methodSignature methodSignature
     * @return: 真实的url
     */
    private String getUrl(MethodSignature methodSignature, HttpServletRequest request) {
        String url;
        // 如果含有@RequestMapping注解，则为 http 请求;否则即为金融网关接口
        if (methodSignature.getMethod().isAnnotationPresent(RequestMapping.class)) {
            url = request.getRequestURI();
        } else {
            url = methodSignature.getDeclaringType().getSimpleName() + "/" + methodSignature.getMethod().getName();
        }
        return url;
    }


}
