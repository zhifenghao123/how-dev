package com.howdev.test.condition;

import java.util.ArrayList;
import java.util.List;

/**
 * ComplexExpression class
 *
 * @author haozhifeng
 * @date 2024/03/11
 */
public class ComplexExpression extends Expression {
    /**
     * 简单表达式列表
     */
    private List<Expression> expressions = new ArrayList<>();
    /**
     * 逻辑连接符（如 "and", "or"）
     */
    private String link;

    public ComplexExpression() {
        this.setExprType(COMPLEX_EXPRESSION_TYPE);
    }

    public ComplexExpression(List<Expression> expressions, String link) {
        this.expressions = expressions;
        this.link = link;
        this.setExprType(COMPLEX_EXPRESSION_TYPE);

    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }
    public List<Expression> getExpressions() {
        return expressions;
    }
    public void setExpressions(List<Expression> expressions) {
        this.expressions = expressions;
    }
    public void addExpression(Expression expression) {
        this.expressions.add(expression);
    }
}
