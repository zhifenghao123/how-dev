package com.howdev.manage.util.log.core;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * LogMessageContainer class
 *
 * @author haozhifeng
 * @date 2023/12/07
 */
public class LogMessageContainer {

    public static String getFormattedMessage(ErrorCodeEnum errorCodeEnum, Object...args) {
        return getFormattedMessage(errorCodeEnum.getErrorMessage(), args);
    }

    private static String getFormattedMessage(String message,  Object...args) {
        FormattedLogMessage fMessage = new FormattedLogMessage(message, args);

        String strMsg = fMessage.formattedMsg;

        if (fMessage.t != null) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            fMessage.t.printStackTrace(pw);
            pw.close();

            strMsg += "\r\n" + sw;
        }
        return strMsg;
    }
}
