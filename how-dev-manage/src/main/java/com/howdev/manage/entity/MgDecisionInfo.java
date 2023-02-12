package com.howdev.manage.entity;

import java.io.Serializable;
import java.util.Date;

/**
 * mg_decision_info
 * @author 
 */
public class MgDecisionInfo implements Serializable {
    /**
     * 自增id
     */
    private Long id;

    /**
     * 决策键名
     */
    private String decisionKey;

    /**
     * 决策类型
     */
    private String decisionType;

    /**
     * 决策名称
     */
    private String decisionName;

    /**
     * 决策描述
     */
    private String decisionDesc;

    /**
     * 决策当前版本号
     */
    private Integer version;

    /**
     * 是否启用 0-禁用, 1-启用
     */
    private Byte enable;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 操作人
     */
    private String creator;

    /**
     * 操作人
     */
    private String modifier;

    /**
     * 租户
     */
    private String bizLine;

    private static final long serialVersionUID = 1L;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDecisionKey() {
        return decisionKey;
    }

    public void setDecisionKey(String decisionKey) {
        this.decisionKey = decisionKey;
    }

    public String getDecisionType() {
        return decisionType;
    }

    public void setDecisionType(String decisionType) {
        this.decisionType = decisionType;
    }

    public String getDecisionName() {
        return decisionName;
    }

    public void setDecisionName(String decisionName) {
        this.decisionName = decisionName;
    }

    public String getDecisionDesc() {
        return decisionDesc;
    }

    public void setDecisionDesc(String decisionDesc) {
        this.decisionDesc = decisionDesc;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public Byte getEnable() {
        return enable;
    }

    public void setEnable(Byte enable) {
        this.enable = enable;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public String getModifier() {
        return modifier;
    }

    public void setModifier(String modifier) {
        this.modifier = modifier;
    }

    public String getBizLine() {
        return bizLine;
    }

    public void setBizLine(String bizLine) {
        this.bizLine = bizLine;
    }
}