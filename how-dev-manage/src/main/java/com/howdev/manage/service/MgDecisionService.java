package com.howdev.manage.service;

import com.howdev.manage.entity.MgDecisionInfo;

import java.util.List;

/**
 * MgDecisionService class
 *
 * @author haozhifeng
 * @date 2023/02/10
 */
public interface MgDecisionService {
    /**
     * create
     *
     * @param mgDecisionInfo mgDecisionInfo
     * @return:
     * @author: haozhifeng
     */
    boolean create(MgDecisionInfo mgDecisionInfo);
    /**
     * selectAll
     * 
     * @param  
     * @return: 
     * @author: haozhifeng     
     */
    List<MgDecisionInfo> selectAll();
}
