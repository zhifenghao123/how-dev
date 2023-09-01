package com.howdev.test.business.controller;

import com.howdev.common.util.JacksonUtil;
import com.howdev.test.business.component.CalculateProcessor1;
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

    @Autowired
    private CalculateProcessor1 calculateProcessor1;



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

    @ResponseBody
    @RequestMapping(value="cal")
    public String calTest(String type) {
        System.out.println(type);
        String method = "unknow";
        if (type.equals("1")) {
            calculateProcessor1.process1();
            method = "calculateProcessor1.process1()";
        } else {
            calculateProcessor1.process_sub_1();
            method = "calculateProcessor1.process_sub_1()";
        }
        return "OK! \n\r reuqest type = " + type + "\n\n process method = " + method;

    }

}
