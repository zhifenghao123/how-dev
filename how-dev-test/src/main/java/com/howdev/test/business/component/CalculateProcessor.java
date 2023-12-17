package com.howdev.test.business.component;

import com.howdev.test.business.entity.User;
import com.howdev.test.business.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * CalculateProcessor class
 *
 * @author haozhifeng
 * @date 2023/09/01
 */
public abstract class CalculateProcessor {
    //@Autowired
    private UserMapper userMapper;

    @Autowired
    public void setUserMapper(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    /*@Autowired
    public CalculateProcessor(UserMapper userMapper) {
        this.userMapper = userMapper;
    }*/




    public void process1(){
        List<User> users = userMapper.selectAllUsers();
        users.stream().forEach(user -> System.out.println(user));
    }

}
