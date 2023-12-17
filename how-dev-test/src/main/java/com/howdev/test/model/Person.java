package com.howdev.test.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

/**
 * Person class
 *
 * @author haozhifeng
 * @date 2023/07/13
 */
@NoArgsConstructor
@Getter
@Setter
@ToString
public class Person implements Serializable {
    // 去掉后，系统自动生成
    private static final long serialVersionUID = 1L;

    private String name;
    private int age;
    private int gender;
}
