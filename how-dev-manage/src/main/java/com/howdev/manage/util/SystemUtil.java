package com.howdev.manage.util;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * SystemUtil class
 *
 * @author haozhifeng
 * @date 2023/02/12
 */
public final class SystemUtil {
    private static String localIp = null;
    private static String localHostName = null;

    static {
        try {
            InetAddress addr = InetAddress.getLocalHost();
            localIp = addr.getHostAddress();
            localHostName = addr.getHostName();
        } catch (UnknownHostException var1) {
            localIp = "";
            localHostName = "";
        }
    }

    public static String getLocalIp() {
        return localIp;
    }

    public static String getLocalHostName() {
        return localHostName;
    }



}
