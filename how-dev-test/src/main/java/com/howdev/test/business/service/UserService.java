package com.howdev.test.business.service;

import com.howdev.test.business.entity.User;

import java.util.List;

/**
 * UserService class
 *
 * @author haozhifeng
 * @date 2023/07/18
 */
public interface UserService {
    User findUserById(long id);

    List<User> findAllUsers();

    boolean addUser(User user);
}
