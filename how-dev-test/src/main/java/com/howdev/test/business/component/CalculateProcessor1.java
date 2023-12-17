package com.howdev.test.business.component;

import com.howdev.test.business.entity.User;
import com.howdev.test.business.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * CalculateProcessor1 class
 *
 * @author haozhifeng
 * @date 2023/09/01
 */
@Service
public class CalculateProcessor1 extends CalculateProcessor {
    //@Autowired
    private UserMapper userMapper;

    @Autowired
    public void setUserMapper(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

   /* @Autowired
    public CalculateProcessor1(UserMapper userMapper, UserMapper userMapper1) {
        super(userMapper);
        this.userMapper = userMapper1;
    }
*/


    public void process_sub_1(){
        User user = userMapper.selectByPrimaryKey(1);
        System.out.println(user);
    }



}
