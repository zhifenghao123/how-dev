package com.howdev.manage.util;

/**
 * Assert0 class
 *
 * @author haozhifeng
 * @date 2023/02/12
 */
public class Assert0 {

    public static void notNull(Object object) {
        notNull(object, "[Assertion failed] - this argument is required; it must not be null");
    }

    public static void notNull(Object object, String message) {
        if (object == null) {
            throw new IllegalArgumentException(message);
        }
    }

}
