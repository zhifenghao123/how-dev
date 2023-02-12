package com.howdev.api.model;

import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Map;

/**
 * DecisionRequest class
 *
 * @author haozhifeng
 * @date 2023/01/13
 */
@Validated
public class DecisionRequest {
    /**
     * 请求id
     */
    @NotBlank(message = "requestId is blank!")
    private String requestId;

    /**
     * decisionType类型
     */
    @NotBlank(message = "decisionType is blank!")
    private String decisionType;

    /**
     * 决策id
     */
    @NotBlank(message = "decisionId is blank!")
    private String decisionId;

    /**
     * 请求业务参数
     */
    @Valid
    @NotNull(message = "params is null!")
    private Map<String, Object> params;
}
