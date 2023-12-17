package com.howdev.test.business.controller;

import com.howdev.common.util.FileReadUtil;
import com.howdev.common.util.FileWriteUtil;
import com.howdev.common.util.JacksonUtil;
import com.howdev.test.business.component.CalculateProcessor1;
import com.howdev.test.business.entity.User;
import com.howdev.test.business.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Base64;
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



    @ResponseBody
    @RequestMapping(value="decodeTest")
    public String decodeTest() {
        User user = new User();
        user.setId(00000);
        user.setName("我我我我");
        user.setBirthDate("1997-04-04");
        user.setGender(0);
        String userJson = JacksonUtil.toJson(user);
        String base64Encode = getBase64Encode(userJson);
        String base64Decode = getBase64Decode(base64Encode);
        System.out.println(base64Encode);
        System.out.println("---");
        System.out.println(base64Decode);
        return base64Decode;

    }

    @ResponseBody
    @RequestMapping(value="decodeTestWithReadFile")
    public String decodeTestWithReadFile() {
        User user = new User();
        user.setId(00000);
        user.setName("我我我我");
        user.setBirthDate("1997-04-04");
        user.setGender(0);
        String userJson = JacksonUtil.toJson(user);
        String base64Encode = getBase64Encode(userJson);
        String fileName = "./how-dev-test/data_dir/person-2.txt";
        try {
            FileWriteUtil.writeToFileWithFiles(fileName,base64Encode);
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }

        String fileContent = null;
        try {
            fileContent = FileReadUtil.readByByteArray(fileName);
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
        String base64Decode = getBase64Decode(fileContent);
        System.out.println(base64Encode);
        System.out.println("---");
        System.out.println(base64Decode);
        return base64Decode;

    }

    // 加密
    public static String getBase64Encode(String originStr) {
        byte[] bytes = null;
        String encodedDataString = null;
        try {
            bytes = originStr.getBytes("utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        if (bytes != null) {
            encodedDataString = Base64.getMimeEncoder().encodeToString(bytes);
        }
        return encodedDataString;
    }

    // 解密
    public static String getBase64Decode(String encodedDataString) {
        byte[] bytes = null;
        String decodedDataString = null;
        if (encodedDataString != null) {
            try {
                bytes = Base64.getMimeDecoder().decode(encodedDataString);
                decodedDataString = new String(bytes, "utf-8");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return decodedDataString;
    }



}
