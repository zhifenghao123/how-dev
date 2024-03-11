package com.howdev.test.condition;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * Expression class
 *
 * @author haozhifeng
 * @date 2024/03/08
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "exprType")
@JsonSubTypes({
        @JsonSubTypes.Type(value = SimpleExpression.class, name = "simple"),
        @JsonSubTypes.Type(value = ComplexExpression.class, name = "complex")
})
public abstract class Expression {
    public static final String SIMPLE_EXPRESSION_TYPE = "simple";
    public static final String COMPLEX_EXPRESSION_TYPE = "complex";

    private String exprType;

    public String getExprType() {
        return exprType;
    }

    public void setExprType(String exprType) {
        this.exprType = exprType;
    }
}
