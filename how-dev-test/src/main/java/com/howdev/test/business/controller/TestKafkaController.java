package com.howdev.test.business.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * TestKafkaController class
 *
 * @author haozhifeng
 * @date 2024/03/18
 */
@Controller
@RequestMapping("kafkatest")
public class TestKafkaController {
    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @RequestMapping("/send")
    public String send(@RequestParam("msg") String msg) {
        kafkaTemplate.send("hao-test-topic", msg);
        return "success";
    }
}
