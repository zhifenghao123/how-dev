package com.howdev.test.validate;

import java.util.ArrayList;
import java.util.Collection;

/**
 * ValidateUtil class
 *
 * @author haozhifeng
 * @date 2023/06/14
 */
public class ValidateUtil {
    public static void checkNullWithError(Object object, Object... args) throws Exception {
        if (object == null) {
            throw new Exception();
        }
        /*if (object instanceof CharSequence && StringUtils.isBlank((CharSequence) object)) {
            throw new Exception();
        }*/
        if (object instanceof Collection && ((Collection)object).size() == 0) {
            System.out.println("0000");
            throw new Exception();
        }
    }

    public static void main(String[] args) throws Exception {
        //checkNullWithError(null);
        checkNullWithError(new ArrayList<Integer>());
    }
}
