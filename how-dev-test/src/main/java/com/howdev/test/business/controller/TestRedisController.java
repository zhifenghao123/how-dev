package com.howdev.test.business.controller;

import com.howdev.test.redis.RedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * TestRedisController class
 *
 * @author haozhifeng
 * @date 2024/02/28
 */
@Controller
@RequestMapping("redistest")
public class TestRedisController {
    @Autowired
    private RedisService redisService;

    @RequestMapping("lock")
    @ResponseBody
    public Boolean lock() {
         Boolean result = redisService.lock("name", "haozhi", 100);
        return result;
    }

    @RequestMapping("unlock")
    @ResponseBody
    public Boolean unlock() {
        Boolean result = redisService.unlock("name", "haozhifeng");
        return result;
    }
}
