package com.howdev.api.model;

import java.util.Map;

/**
 * DecisionResponse class
 *
 * @author haozhifeng
 * @date 2023/01/13
 */
public class DecisionResponse {
    /**
     * decisionType类型
     */
    private String decisionType;

    /**
     * 决策id
     */
    private String decisionId;

    /**
     * 决策结果
     */
    private String decisionResult;

    /**
     * 决策结果的附加信息
     */
    private Map<String, String> result;

}
