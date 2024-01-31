package com.howdev.app.po;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * async_decision_task
 * @author 
 */
@Data
public class AsyncDecisionTaskPo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 自增ID
     */
    private Long id;

    /**
     * 决策ID
     */
    private String decisionId;

    /**
     * 阶段，如果一个决策需要由多个不连续的若干阶段处理，而每个阶段需要由其他系统或时机异步触发，则对应待触发的阶段单独有一条异步任务
     */
    private String stage;

    /**
     * 决策的key
     */
    private String decisionKey;

    /**
     * 队列类型,normal:普通队列 retry:重试队列
     */
    private String queueType;

    /**
     * 状态 0:初始状态 1:处理中 2:处理成功 3:处理失败 4:已删除(终止不再重试)
     */
    private Byte status;

    /**
     * 机房名称tag
     */
    private String idcTag;

    /**
     * 请求参数
     */
    private String requestParams;

    /**
     * 系统扩展参数
     */
    private String systemParams;

    /**
     * 锁定时间
     */
    private Date lockTime;

    /**
     * 锁定机器IP
     */
    private String lockIp;

    /**
     * 已重试次数
     */
    private Integer retryCount;

    /**
     * 重试执行时间
     */
    private Date retryTime;

    /**
     * 异常码
     */
    private String errorCode;

    /**
     * 异常消息
     */
    private String errorMsg;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 乐观锁版本号
     */
    private Integer version;
}