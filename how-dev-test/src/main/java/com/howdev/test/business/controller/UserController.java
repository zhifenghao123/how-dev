package com.howdev.test.business.controller;

import com.howdev.common.util.JacksonUtil;
import com.howdev.test.business.entity.User;
import com.howdev.test.business.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * UserController class
 *
 * @author haozhifeng
 * @date 2023/07/18
 */
@Controller
@RequestMapping("user")
public class UserController {
    @Autowired
    private UserService userService;

    @ResponseBody
    @RequestMapping(value = "getAll")
    public String getAllUsers() {
        List<User> allUsers = userService.findAllUsers();
        return JacksonUtil.toJson(allUsers);
    }

    @ResponseBody
    @RequestMapping(value="addUser")
    public boolean addUser(@RequestBody User user) {
        boolean addUserResult = userService.addUser(user);
        return addUserResult;
    }
}
