package com.howdev.common.exceptions;

import java.text.MessageFormat;

/**
 * FormattedLogMessage class
 *
 * @author haozhifeng
 * @date 2023/12/07
 */
public class FormattedLogMessage {

    /**
     * 格式化后的文本内容
     */
    String formattedMsg;

    /**
     * 文本用到的参数列表
     */
    private Object[] formattedArgs;

    /**
     * 异常信息, 从最后一个参数中判断获取
     */
    Throwable t;

    FormattedLogMessage(String message, Object[] args) {
        if (args == null || args.length == 0) {
            this.formattedArgs = null;
            this.t = null;
        } else {
            // 解析异常和参数列表
            buildThrowableAndArgs(args);
        }

        buildMessage(message);
    }

    /**
     * 解析异常信息和格式化参数列表
     *
     * @param args 所有原始参数
     */
    private void buildThrowableAndArgs(Object[] args) {
        // 如果最后一个参数是异常, 获取异常信息
        if (args[args.length - 1] instanceof Throwable) {
            this.t = (Throwable) args[args.length - 1];
            // 除了异常信息之外, 仍然有参数
            if (args.length > 1) {
                this.formattedArgs = new Object[args.length - 1];
                System.arraycopy(args, 0, this.formattedArgs, 0, this.formattedArgs.length);
            } else {
                this.formattedArgs = null;
            }
        } else {
            this.formattedArgs = args;
            this.t = null;
        }
    }

    /**
     * 生成文本内容
     *
     * @param message 消息模板
     */
    private void buildMessage(String message) {
        // 格式化参数不为空时, 处理格式化参数
        if (this.formattedArgs != null) {
            for (int i = 0; i < formattedArgs.length; i++) {
                Object arg = formattedArgs[i];
                // 数字转换为字符串
                if (arg instanceof Number) {
                    formattedArgs[i] = arg.toString();
                }
                message = message.replace("{" + i + "_sec}", "{" + i + "}");
            }
            message = message.replace("_sec", "");
        }

        // 格式化
        this.formattedMsg = MessageFormat.format(message, formattedArgs);
    }
}
