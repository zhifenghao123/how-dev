package com.howdev.manage.util.log;

import com.howdev.manage.util.Assert0;
import org.slf4j.Logger;
import org.slf4j.MDC;

import java.util.Arrays;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * LoggerProxy class
 *
 * @author haozhifeng
 * @date 2023/02/12
 */
public final class LoggerProxy {

    public static final String METHOD_NAME_SPLIT = "...";
    public static final String METHOD_BLANK_SPLIT=" ";
    public static final String METHOD_STANDARD_SPLIT = ", ";


    public static enum ErrorKnown {
        KNOWN, UNKNOWN
    }

    public static enum LoggerTemplate {
        RecievedMessage_HTTP(MethodNameEnum.receivedMessage.name(), MethodEnum.HTTP.name(),
                generateTemplate("methodName", "method", "url", "sourceIp", "localIp", "request")),
        RecievedMessage_RPC(MethodNameEnum.receivedMessage.name(), MethodEnum.RPC.name(),
                generateTemplate("methodName", "method", "url", "sourceIp", "localIp", "request")),
        RecievedMessage_SCHEDULE(MethodNameEnum.receivedMessage.name(), MethodEnum.SCHEDULE.name(),
                generateTemplate("methodName", "method", "url", "sourceIp", "localIp", "request")),
        processMessageFinished_HTTP(MethodNameEnum.processMessageFinished.name(), MethodEnum.HTTP.name(),
                generateTemplate("methodName", "method", "url", "sourceIp", "localIp", "request",
                        "response", "retCode", "costs")),
        processMessageFinished_KAFKA(MethodNameEnum.processMessageFinished.name(), MethodEnum.KAFKA.name(),
                generateTemplate("methodName", "method", "url", "localIp", "request", "response", "retCode", "costs")),
        processMessageFinished_RPC(MethodNameEnum.processMessageFinished.name(), MethodEnum.RPC.name(),
                generateTemplate("methodName", "method", "url", "sourceIp", "sourceApp", "localIp", "request",
                        "response", "retCode", "costs")),
        processMessageFinished_SCHEDULE(MethodNameEnum.processMessageFinished.name(), MethodEnum.SCHEDULE.name(),
                generateTemplate("methodName", "method", "url", "localIp", "request", "response", "retCode", "costs")),
        requestService_HTTP(MethodNameEnum.requestService.name(), MethodEnum.HTTP.name(),
                generateTemplate("methodName", "method", "url", "serviceName", "remoteIp", "localIp", "request",
                        "result", "errorCode", "costs")),
        requestService_KAFKA(MethodNameEnum.requestService.name(), MethodEnum.KAFKA.name(),
                generateTemplate("methodName", "method", "url", "serviceName", "localIp", "request", "result",
                        "errorCode", "costs")),
        requestService_RPC(MethodNameEnum.requestService.name(), MethodEnum.RPC.name(),
                generateTemplate("methodName", "method", "url", "serviceName", "remoteIp", "localIp", "request",
                        "result", "errorCode", "costs"));

        private LoggerTemplate(String methodName, String method, String[] logArgs, String[] printArgs) {
            this(methodName, method, generateTemplate(printArgs),
                    (objs) -> {
                        objs = generateObjectArr(objs, methodName, method);
                        Object[] nArgs = new Object[objs.length];
                        for (int i = 0;i < objs.length;i++) {
                            String key = logArgs[i];
                            for (int j = 0;j < printArgs.length;j++) {
                                if (printArgs[j] == key) {
                                    nArgs[j] = objs[i];
                                    break;
                                }
                            }
                        }
                        return nArgs;
                    });
        }

        private LoggerTemplate(String methodName, String method, String template) {
            this(methodName, method, template, (objs) -> generateObjectArr(objs, methodName, method));
        }
        private LoggerTemplate(String methodName, String method,String template,
                               Function<Object[], Object[]> generateArg) {
            this.methodName = methodName;
            this.method = method;
            this.template = template;
            this.generateArg = generateArg;
        }

        private String template;
        private Function<Object[], Object[]> generateArg;
        private String methodName;
        private String method;

        public String getTemplate() {
            return template;
        }

        /**
         * 模板输出日志
         * @param customLog 自定义日志，其中的内容输出再模板之后
         * @param logger 日志对象
         * @param args 输出参数，跳过 methodName 和 method，从第三个开始；如果有自定义日志中的{}，需要在模板输出参数之后
         */
        public void log(String customLog, Logger logger, Object... args) {
            LoggerProxy.log(logger, template + Optional.ofNullable(customLog).orElseGet(String::new),
                    generateArg.apply(args));
        }

        /**
         * 模板输出日志
         * @param logger 日志对象
         * @param args 输出参数，跳过 methodName 和 method，从第三个开始
         */
        public void log(Logger logger, Object... args) {
            log(null, logger, args);
        }

        /**
         * 生成标准化日志模板
         * @param fields 日志输出字段
         * @return
         */
        public static String generateTemplate(String... fields) {
            return Stream.of(fields).map(s -> s + "={}").collect(Collectors.joining(METHOD_STANDARD_SPLIT));
        }

        public static enum MethodNameEnum {
            receivedMessage, processMessageFinished, requestService;
        }

        public static enum MethodEnum {
            HTTP, RPC, KAFKA, SCHEDULE;
        }
    }

    public static Object[] generateObjectArr(Object[] origin, Object... prefix) {
        return Stream.concat(Arrays.stream(prefix), Arrays.stream(origin)).toArray();
    }





    private static final String DEFAULT_VALUE = "NULL";


    /**
     * Is the logger instance enabled for the DEBUG level?
     *
     * @return True if this Logger is enabled for the DEBUG level, false otherwise.
     */
    public static final boolean isDebugEnabled(Logger logger) {
        Assert0.notNull(logger);
        return logger.isDebugEnabled();
    }

    /**
     * Log a message at the DEBUG level.
     *
     * @param msg the message string to be logged
     */
    public static final void debug(Logger logger, String msg) {
        Assert0.notNull(logger);
        logger.debug(msg);
    }

    /**
     * Log a message at the DEBUG level according to the specified format and argument.
     *
     * <p>
     * This form avoids superfluous object creation when the logger is disabled for the DEBUG level.
     * </p>
     *
     * @param format the format string
     * @param arg    the argument
     */
    public static final void debug(Logger logger, String format, Object arg) {
        Assert0.notNull(logger);
        logger.debug(format, arg);
    }

    /**
     * Log a message at the DEBUG level according to the specified format and arguments.
     *
     * <p>
     * This form avoids superfluous object creation when the logger is disabled for the DEBUG level.
     * </p>
     *
     * @param format the format string
     * @param arg1   the first argument
     * @param arg2   the second argument
     */
    public static final void debug(Logger logger, String format, Object arg1, Object arg2) {
        Assert0.notNull(logger);
        logger.debug(format, arg1, arg2);
    }

    /**
     * Log a message at the DEBUG level according to the specified format and arguments.
     *
     * <p>
     * This form avoids superfluous object creation when the logger is disabled for the DEBUG level.
     * </p>
     *
     * @param format   the format string
     * @param argArray an array of arguments
     */
    public static final void debug(Logger logger, String format, Object[] argArray) {
        Assert0.notNull(logger);
        logger.debug(format, argArray);
    }

    /**
     * Log an exception (throwable) at the DEBUG level with an accompanying message.
     *
     * @param msg the message accompanying the exception
     * @param t   the exception (throwable) to log
     */
    public static final void debug(Logger logger, String msg, Throwable t) {
        Assert0.notNull(logger);
        logger.debug(msg, t);
    }

    /**
     * Is the logger instance enabled for the INFO level?
     *
     * @return True if this Logger is enabled for the INFO level, false otherwise.
     */
    public static final boolean isInfoEnabled(Logger logger) {
        Assert0.notNull(logger);
        return logger.isInfoEnabled();
    }

    /**
     * Log a message at the INFO level.
     *
     * @param msg the message string to be logged
     */
    public static final void info(Logger logger, String msg) {
        Assert0.notNull(logger);
        logger.info(msg);
    }

    public static final void info(String methodName, Logger logger, String msg) {
        Assert0.notNull(logger);
        msg = "methodName=" + methodName + METHOD_NAME_SPLIT + msg;
        logger.info(msg);
    }

    /**
     * Log a message at the INFO level according to the specified format and argument.
     *
     * <p>
     * This form avoids superfluous object creation when the logger is disabled for the INFO level.
     * </p>
     *
     * @param format the format string
     * @param arg    the argument
     */
    public static final void info(Logger logger, String format, Object arg) {
        Assert0.notNull(logger);
        logger.info(format, arg);
    }

    public static final void info(String methodName, Logger logger, String format, Object arg) {
        Assert0.notNull(logger);
        format = "methodName=" + methodName + METHOD_NAME_SPLIT + format;
        logger.info(format, arg);
    }

    /**
     * Log a message at the INFO level according to the specified format and arguments.
     *
     * <p>
     * This form avoids superfluous object creation when the logger is disabled for the INFO level.
     * </p>
     *
     * @param format the format string
     * @param arg1   the first argument
     * @param arg2   the second argument
     */
    public static final void info(Logger logger, String format, Object arg1, Object arg2) {
        Assert0.notNull(logger);
        logger.info(format, arg1, arg2);
    }

    public static final void info(String methodName, Logger logger, String format, Object arg1, Object arg2) {
        Assert0.notNull(logger);
        format = "methodName=" + methodName + METHOD_NAME_SPLIT + format;
        logger.info(format, arg1, arg2);
    }

    /**
     * Log a message at the INFO level according to the specified format and arguments.
     *
     * <p>
     * This form avoids superfluous object creation when the logger is disabled for the INFO level.
     * </p>
     *
     * @param format   the format string
     * @param argArray an array of arguments
     */
    public static final void info(Logger logger, String format, Object... argArray) {
        Assert0.notNull(logger);
        logger.info(format, argArray);
    }

    public static final void info(String methodName, Logger logger, String format, Object... argArray) {
        Assert0.notNull(logger);
        format = "methodName=" + methodName + METHOD_NAME_SPLIT + format;
        logger.info(format, argArray);
    }

    public static final void info(String methodName, String method, Logger logger, String format, Object... argArray) {
        Assert0.notNull(logger);
        format="methodName=" + methodName+ METHOD_BLANK_SPLIT + "method=" + method + METHOD_BLANK_SPLIT + format;
        logger.info(format, argArray);
    }

    /**
     * Log an exception (throwable) at the INFO level with an accompanying message.
     *
     * @param msg the message accompanying the exception
     * @param t   the exception (throwable) to log
     */
    public static final void info(Logger logger, String msg, Throwable t) {
        Assert0.notNull(logger);
        logger.info(msg, t);
    }

    public static final void info(String methodName, Logger logger, String msg, Throwable t) {
        Assert0.notNull(logger);
        msg = "methodName=" + methodName + METHOD_NAME_SPLIT + msg;
        logger.info(msg, t);
    }

    public static final void charge(Logger logger, String format, Object... argArray) {
        MDC.put("charge", "charge");
        logger.info("CHARGE:" + format, argArray);
        MDC.remove("charge");
    }

    /**
     * Is the logger instance enabled for the WARN level?
     *
     * @return True if this Logger is enabled for the WARN level, false otherwise.
     */
    public static final boolean isWarnEnabled(Logger logger) {
        Assert0.notNull(logger);
        return logger.isWarnEnabled();
    }

    /**
     * Log a message at the WARN level.
     *
     * @param msg the message string to be logged
     */
    public static final void warn(Logger logger, String msg) {
        Assert0.notNull(logger);
        logger.warn(msg);
    }

    public static final void warn(String methodName, Logger logger, String msg) {
        Assert0.notNull(logger);
        msg = "methodName=" + methodName + METHOD_NAME_SPLIT + msg;
        logger.warn(msg);
    }

    /**
     * Log a message at the WARN level according to the specified format and argument.
     *
     * <p>
     * This form avoids superfluous object creation when the logger is disabled for the WARN level.
     * </p>
     *
     * @param format the format string
     * @param arg    the argument
     */
    public static final void warn(String methodName, Logger logger, String format, Object arg) {
        Assert0.notNull(logger);
        format = "methodName=" + methodName + METHOD_NAME_SPLIT + format;
        logger.warn(format, arg);
    }

    /**
     * Log a message at the WARN level according to the specified format and arguments.
     *
     * <p>
     * This form avoids superfluous object creation when the logger is disabled for the WARN level.
     * </p>
     *
     * @param format the format string
     * @param arg1   the first argument
     * @param arg2   the second argument
     */
    public static final void warn(Logger logger, String format, Object arg1, Object arg2) {
        Assert0.notNull(logger);
        logger.warn(format, arg1, arg2);
    }

    public static final void warn(String methodName, Logger logger, String format, Object arg1, Object arg2) {
        Assert0.notNull(logger);
        format = "methodName=" + methodName + METHOD_NAME_SPLIT + format;
        logger.warn(format, arg1, arg2);
    }

    /**
     * Log a message at the WARN level according to the specified format and arguments.
     *
     * <p>
     * This form avoids superfluous object creation when the logger is disabled for the WARN level.
     * </p>
     *
     * @param format   the format string
     * @param argArray an array of arguments
     */
    public static final void warn(Logger logger, String format, Object[] argArray) {
        Assert0.notNull(logger);
        logger.warn(format, argArray);
    }

    /**
     * Log a message at the WARN level according to the specified format and arguments.
     * and add method name
     *
     * @param format    the format string
     * @param arguments a list of 3 or more arguments
     */
    public static final void warn(String methodName, Logger logger, String format, Object... arguments) {
        Assert0.notNull(logger);
        format = "methodName=" + methodName + METHOD_NAME_SPLIT + format;
        logger.warn(format, arguments);
    }

    /**
     * Log an exception (throwable) at the WARN level with an accompanying message.
     *
     * @param msg the message accompanying the exception
     * @param t   the exception (throwable) to log
     */
    public static final void warn(Logger logger, String msg, Throwable t) {
        Assert0.notNull(logger);
        logger.warn(msg, t);
    }

    public static final void warn(String methodName, Logger logger, String msg, Throwable t) {
        Assert0.notNull(logger);
        msg = "methodName=" + methodName + METHOD_NAME_SPLIT + msg;
        logger.warn(msg, t);
    }

    /**
     * Is the logger instance enabled for the ERROR level?
     *
     * @return True if this Logger is enabled for the ERROR level, false otherwise.
     */
    public static final boolean isErrorEnabled(Logger logger) {
        Assert0.notNull(logger);
        return logger.isErrorEnabled();
    }

    /**
     * Log a message at the ERROR level.
     *
     * @param msg the message string to be logged
     */
    public static final void error(Logger logger, String msg) {
        Assert0.notNull(logger);
        logger.error(msg);
    }

    public static final void error(String methodName, ErrorKnown yeOrNo, Logger logger, String msg) {
        Assert0.notNull(logger);
        MDC.put("errorknown", null == yeOrNo ? "ErrorKnownNull" : yeOrNo.name());
        msg = "methodName=" + methodName + METHOD_NAME_SPLIT + msg;
        logger.error(msg);
        MDC.remove("errorknown");
    }

    /**
     * Log a message at the ERROR level according to the specified format and argument.
     *
     * <p>
     * This form avoids superfluous object creation when the logger is disabled for the ERROR level.
     * </p>
     *
     * @param format the format string
     * @param arg    the argument
     */
    public static final void error(Logger logger, String format, Object arg) {
        Assert0.notNull(logger);
        logger.error(format, arg);
    }

    public static final void error(String methodName, ErrorKnown yeOrNo, Logger logger, String format, Object arg) {
        Assert0.notNull(logger);
        MDC.put("errorknown", null == yeOrNo ? "ErrorKnownNull" : yeOrNo.name());
        format = "methodName=" + methodName + METHOD_NAME_SPLIT + format;
        logger.error(format, arg);
        MDC.remove("errorknown");
    }

    /**
     * Log a message at the ERROR level according to the specified format and arguments.
     *
     * <p>
     * This form avoids superfluous object creation when the logger is disabled for the ERROR level.
     * </p>
     *
     * @param format the format string
     * @param arg1   the first argument
     * @param arg2   the second argument
     */
    public static final void error(Logger logger, String format, Object arg1, Object arg2) {
        Assert0.notNull(logger);
        logger.error(format, arg1, arg2);
    }

    public static final void error(String methodName, ErrorKnown yeOrNo, Logger logger, String format, Object arg1,
                                   Object arg2) {
        Assert0.notNull(logger);
        MDC.put("errorknown", null == yeOrNo ? "ErrorKnownNull" : yeOrNo.name());
        format = "methodName=" + methodName + METHOD_NAME_SPLIT + format;
        logger.error(format, arg1, arg2);
        MDC.remove("errorknown");
    }

    /**
     * Log a message at the ERROR level according to the specified format and arguments.
     *
     * <p>
     * This form avoids superfluous object creation when the logger is disabled for the ERROR level.
     * </p>
     *
     * @param format   the format string
     * @param argArray an array of arguments
     */
    public static final void error(Logger logger, String format, Object[] argArray) {
        Assert0.notNull(logger);
        logger.error(format, argArray);
    }

    /**
     * replace error argArray
     *
     * @param methodName
     * @param yeOrNo     ErrorKnown enum
     * @param logger
     * @param format
     * @param arguments  a list of 3 or more arguments
     */
    public static final void error(String methodName, ErrorKnown yeOrNo, Logger logger, String format,
                                   Object... arguments) {
        Assert0.notNull(logger);
        MDC.put("errorknown", null == yeOrNo ? "ErrorKnownNull" : yeOrNo.name());
        format = "methodName=" + methodName + METHOD_NAME_SPLIT + format;
        logger.error(format, arguments);
        MDC.remove("errorknown");
    }

    /**
     * Log an exception (throwable) at the ERROR level with an accompanying message.
     *
     * @param msg the message accompanying the exception
     * @param t   the exception (throwable) to log
     */
    public static final void error(Logger logger, String msg, Throwable t) {
        Assert0.notNull(logger);
        logger.error(msg, t);
    }

    public static final void error(String methodName, ErrorKnown yeOrNo, Logger logger, String msg, Throwable t) {
        Assert0.notNull(logger);
        MDC.put("errorknown", null == yeOrNo ? "ErrorKnownNull" : yeOrNo.name());
        msg = "methodName=" + methodName + METHOD_NAME_SPLIT + msg;
        logger.error(msg, t);
        MDC.remove("errorknown");
    }

    /**
     * 输出 sia 标准化日志
     * @param logger 日志对象
     * @param format 日志格式
     * @param args 输出参数
     */
    public static final void log(Logger logger, String format, Object... args) {
        logger.info(format, args);
    }
}
