package com.howdev.manage.vo;

import java.util.Date;

/**
 * DownLoadParam class
 *
 * @author haozhifeng
 * @date 2023/12/07
 */
public class DownLoadParam {
    /**
     * 一级类型
     */
    private String firstLevelType;
    /**
     * 二级类型
     */
    private String secondLevelType;
    /**
     * 观察日
     */
    private Date observeDate;

    public String getFirstLevelType() {
        return firstLevelType;
    }

    public void setFirstLevelType(String firstLevelType) {
        this.firstLevelType = firstLevelType;
    }

    public String getSecondLevelType() {
        return secondLevelType;
    }

    public void setSecondLevelType(String secondLevelType) {
        this.secondLevelType = secondLevelType;
    }

    public Date getObserveDate() {
        return observeDate;
    }

    public void setObserveDate(Date observeDate) {
        this.observeDate = observeDate;
    }
}
