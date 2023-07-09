package com.howdev.manage.controller;

import com.howdev.manage.annotation.ApiException;
import com.howdev.manage.service.CalculateService;
import com.howdev.manage.vo.BaseResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * CalculateController class
 *
 * @author haozhifeng
 * @date 2023/06/08
 */
@Controller
@RequestMapping("calculate/manage")
@ApiException
public class CalculateController {
    private CalculateService calculateService;

    @Autowired
    public void setCalculateService(CalculateService calculateService) {
        this.calculateService = calculateService;
    }

    @ResponseBody
    @RequestMapping(value = "calculate")
    public BaseResponse calculate() {
        int calculate = calculateService.calculate();
        return BaseResponse.newSuccResponse(calculate);
    }

    @ResponseBody
    @RequestMapping(value = "calculate2")
    public BaseResponse calculate2() {
        int calculate = calculateService.calculate2();
        return BaseResponse.newSuccResponse(calculate);
    }
}
