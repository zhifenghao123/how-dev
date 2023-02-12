package com.howdev.manage.service.impl;

import com.howdev.manage.entity.MgDecisionInfo;
import com.howdev.manage.mapper.MgDecisionInfoMapper;
import com.howdev.manage.service.MgDecisionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * MgDecisionServiceImpl class
 *
 * @author haozhifeng
 * @date 2023/02/10
 */
@Service
public class MgDecisionServiceImpl implements MgDecisionService {
    private MgDecisionInfoMapper mgDecisionInfoMapper;

    @Autowired
    public void setMgDecisionInfoMapper(MgDecisionInfoMapper mgDecisionInfoMapper) {
        this.mgDecisionInfoMapper = mgDecisionInfoMapper;
    }

    @Override
    public boolean create(MgDecisionInfo mgDecisionInfo) {
        Date current = new Date();
        String operator = "haozhifeng";
        mgDecisionInfo.setCreateTime(current);
        mgDecisionInfo.setUpdateTime(current);
        mgDecisionInfo.setCreator(operator);
        mgDecisionInfo.setModifier(operator);
        int insert = mgDecisionInfoMapper.insert(mgDecisionInfo);
        return insert == 1;
    }
}
