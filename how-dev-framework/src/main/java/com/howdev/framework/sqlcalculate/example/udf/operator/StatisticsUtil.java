package com.howdev.framework.sqlcalculate.example.udf.operator;

import org.apache.commons.collections4.CollectionUtils;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * StatisticsUtil class
 *
 * @author haozhifeng
 * @date 2023/12/16
 */
public class StatisticsUtil {
    public static BigDecimal calculateMedian(List<BigDecimal> numbers) {
        if (CollectionUtils.isEmpty(numbers)) {
            return BigDecimal.ZERO;
        }

        // 排序数字数组
        Collections.sort(numbers);

        // 计算中位数
        BigDecimal median;
        int count = numbers.size();
        if (count % 2 == 0) {
            // 如果数组长度是偶数，则中位数是中间两个数的平均值
            BigDecimal number1 = numbers.get(count / 2 - 1);
            BigDecimal number2 = numbers.get(count / 2 );
            BigDecimal add = number1.add(number2);
            median = add.divide(BigDecimal.valueOf(2));
        } else {
            // 如果数组长度是奇数，则中位数是中间的那个数
            median = numbers.get(count / 2 );
        }
        return median;
    }

    public static BigDecimal calculateMedian(double[] numbers) {
        if (numbers == null || numbers.length == 0) {
            return BigDecimal.ZERO;
        }
        List<BigDecimal> collect = Arrays.stream(numbers)
                .mapToObj(BigDecimal::valueOf)
                .collect(Collectors.toList());
        return calculateMedian(collect);
    }

    public static void main(String[] args) {
        double[] doubleVals = Arrays.stream(new double[]{10, 20, 12, 32, 5}).toArray();
        System.out.println(calculateMedian(doubleVals));
    }
}
