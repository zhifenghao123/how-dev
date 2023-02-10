package com.howdev.manage.controller;

import com.howdev.manage.entity.MgDecisionInfo;
import com.howdev.manage.vo.BaseResponse;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * MgDecisionController class
 *
 * @author haozhifeng
 * @date 2023/02/10
 */
@Controller
@RequestMapping("decision/manage")
public class MgDecisionController {

    @ResponseBody
    @RequestMapping(value = "create")
    public BaseResponse create(@RequestBody MgDecisionInfo mgDecisionInfo) {
        return BaseResponse.newSuccResponse(null);
    }
}
