package com.howdev.framework.sqlcalculate.example.udf.h2;

import com.howdev.framework.sqlcalculate.jdbc.udf.AbstractH2Udf;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * H2NumToStrFunction class
 * 输入数字, 返回四舍五入保留2位小数点的字符串,如num_to_str(13.1376)返回13.14
 * 使用示例：num_to_str(13.1376)
 *
 * @author haozhifeng
 * @date 2023/12/11
 */
public class H2NumToStrFunction extends AbstractH2Udf {
    private static final String METHOD_NAME = "numToStr";
    private static final String UDF_NAME = "num_to_str";

    @Override
    public String getUdfName() {
        return UDF_NAME;
    }

    /**
     * h2自定义函数注册时，提供的函数名为:类的全限定名+函数名
     * 该方法返回格式类似com.howdev.sqlcalculate.jdbc.udf.Test.function
     */
    @Override
    public String getClassAndMethodName() {
        return getClass().getName() + "." + METHOD_NAME;
    }


    /**
     * numToStr
     * h2自定义函数必须是public static的
     *
     * @param num num
     * @return:
     * @author: haozhifeng
     */
    public static String numToStr(Double num) {
        if (num == null) {
            return null;
        }
        BigDecimal decimalNum = new BigDecimal(num);
        return decimalNum.setScale(2, RoundingMode.HALF_UP).toPlainString();
    }
}
