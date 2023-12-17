package com.howdev.test.business.util;

import org.apache.commons.lang3.time.DateUtils;

import java.sql.DatabaseMetaData;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

/**
 * RandomUtil class
 *
 * @author haozhifeng
 * @date 2023/07/18
 */
public class RandomUtil {
    private static long RANDOM_DATE_START_TIME;
    private static long RANDOM_DATE_END_TIME;

    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");



    static {
        try {
            Date randomDateStart = DateUtils.parseDate("1980-01-01", "YYYY-MM-dd");
            Date randomDateEnd = DateUtils.parseDate("2010-01-01", "YYYY-MM-dd");
            RANDOM_DATE_START_TIME = randomDateStart.getTime();
            RANDOM_DATE_END_TIME = randomDateEnd.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public static String randomDate(long randomDateStartTime, long randomDateEndTime){
        if (randomDateStartTime == 0L) {
            randomDateEndTime = RANDOM_DATE_START_TIME;
        }
        if (randomDateEndTime == 0L) {
            randomDateEndTime = RANDOM_DATE_END_TIME;
        }
        long currentTimeMillis = System.currentTimeMillis();
        long  randomNum  = currentTimeMillis % (randomDateEndTime - randomDateStartTime) + randomDateStartTime;

        return sdf.format(randomNum);

    }


    public static void main(String[] args) {
        System.out.println(randomDate(0L, 0L));
    }
}
