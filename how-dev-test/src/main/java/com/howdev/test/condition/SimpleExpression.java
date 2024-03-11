package com.howdev.test.condition;

/**
 * SimpleExpression class
 *
 * @author haozhifeng
 * @date 2024/03/11
 */
public class SimpleExpression extends Expression {
    /**
     * 变量
     */
    private String var;
    /**
     * 操作符
     */
    private String operator;
    /**
     * 操作数
     */
    private String operand;

    public SimpleExpression() {
        this.setExprType(SIMPLE_EXPRESSION_TYPE);
    }

    public SimpleExpression(String var, String operator, String operand) {
        this.var = var;
        this.operator = operator;
        this.operand = operand;
        this.setExprType(SIMPLE_EXPRESSION_TYPE);
    }

    public String getVar() {
        return var;
    }

    public void setVar(String var) {
        this.var = var;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public String getOperand() {
        return operand;
    }

    public void setOperand(String operand) {
        this.operand = operand;
    }
}
