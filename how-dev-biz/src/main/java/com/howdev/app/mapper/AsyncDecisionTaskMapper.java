package com.howdev.app.mapper;

import com.howdev.app.po.AsyncDecisionTaskPo;

public interface AsyncDecisionTaskMapper {
    int deleteByPrimaryKey(Long id);

    int insert(AsyncDecisionTaskPo record);

    int insertSelective(AsyncDecisionTaskPo record);

    AsyncDecisionTaskPo selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(AsyncDecisionTaskPo record);

    int updateByPrimaryKey(AsyncDecisionTaskPo record);
}