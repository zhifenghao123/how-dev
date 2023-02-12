package com.howdev.manage.mapper;

import com.howdev.manage.entity.MgDecisionInfo;

public interface MgDecisionInfoMapper {
    int deleteByPrimaryKey(Long id);

    int insert(MgDecisionInfo record);

    int insertSelective(MgDecisionInfo record);

    MgDecisionInfo selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(MgDecisionInfo record);

    int updateByPrimaryKey(MgDecisionInfo record);
}