package com.howdev.manage.controller;

import com.howdev.manage.annotation.ApiException;
import com.howdev.manage.annotation.SwitchDataSourceTag;
import com.howdev.manage.entity.MgDecisionInfo;
import com.howdev.manage.service.MgDecisionService;
import com.howdev.manage.vo.BaseResponse;
import org.springframework.beans.factory.annotation.Autowired;
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
@ApiException
public class MgDecisionController {
    private MgDecisionService mgDecisionService;

    @Autowired
    public void setMgDecisionService(MgDecisionService mgDecisionService) {
        this.mgDecisionService = mgDecisionService;
    }

    @ResponseBody
    @RequestMapping(value = "create")
    @SwitchDataSourceTag(operateDatabase = SwitchDataSourceTag.DATABASE_DECISION_MANAGE)
    public BaseResponse create(@RequestBody MgDecisionInfo request) {
        mgDecisionService.create(request);
        return BaseResponse.newSuccResponse(request);
    }
}
