package com.howdev.app.mapper;

import com.howdev.app.po.AsyncDecisionTaskPo;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AsyncDecisionTaskMapper {
    int deleteByPrimaryKey(Long id);

    int insert(AsyncDecisionTaskPo record);

    int insertSelective(AsyncDecisionTaskPo record);

    AsyncDecisionTaskPo selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(AsyncDecisionTaskPo record);

    int updateByPrimaryKey(AsyncDecisionTaskPo record);

    /**
     * 查询初始状态的待执行异步任务集合同时满足如下条件的5条记录（如果多于5条）：
     * （1）queue_type = "normal" 且 stage = "STAGE_1"，
     * （2）满足如下三个条件之一：
     *      a）status = 0 且 idc = #{idcTag}
     *      b）status = 0 且  update_time 小于 （当前时间 减去 {clusterInterval} 指定的秒数)
     *      c) status = 1 且 lock_time 小于 （当前时间 减去 5分钟)
     *
     * @param idcTag idcTag
     * @param clusterInterval clusterInterval
     * @return:
     * @author: haozhifeng
     */
    List<AsyncDecisionTaskPo> queryInitAsyncTasks(String idcTag, int clusterInterval);
}