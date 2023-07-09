package com.howdev.common.testmodel;

import java.util.Map;

/**
 * RequestData class
 *
 * @author haozhifeng
 * @date 2023/03/21
 */
public class RequestData {
    private String reqId;
    private Map<String, Object> params;

    public String getReqId() {
        return reqId;
    }

    public void setReqId(String reqId) {
        this.reqId = reqId;
    }

    public Map<String, Object> getParams() {
        return params;
    }

    public void setParams(Map<String, Object> params) {
        this.params = params;
    }
}
