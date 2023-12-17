package com.howdev.test;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * AppMain class
 *
 * @author haozhifeng
 * @date 2023/07/18
 */
@SpringBootApplication
@MapperScan("com.howdev..mapper")
public class AppMain {
    public static void main(String[] args) {
        SpringApplication.run(AppMain.class);
    }
}
