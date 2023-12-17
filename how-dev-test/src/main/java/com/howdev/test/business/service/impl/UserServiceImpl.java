package com.howdev.test.business.service.impl;

import com.howdev.test.business.entity.User;
import com.howdev.test.business.mapper.UserMapper;
import com.howdev.test.business.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * UserServiceImpl class
 *
 * @author haozhifeng
 * @date 2023/07/18
 */
@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserMapper userMapper;

    @Override
    public User findUserById(long id) {
        return null;
    }

    @Override
    public List<User> findAllUsers() {
        return userMapper.selectAllUsers();
    }

    @Override
    public boolean addUser(User user) {
        return 1 == userMapper.insert(user);
    }
}
