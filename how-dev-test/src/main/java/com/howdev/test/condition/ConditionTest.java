package com.howdev.test.condition;

import com.howdev.common.util.JacksonUtil;

/**
 * ConditionTest class
 *
 * @author haozhifeng
 * @date 2024/03/11
 */
public class ConditionTest {
    public static void main(String[] args) {
        //test1();
        test1_1();
        //test2();
        test2_1();
        //test3();
        test3_1();
    }

    public static void test1(){
        SimpleExpression simpleExpression1 = new SimpleExpression("age", ">", "20");

        //ComplexExpression complexExpressionFinal = new ComplexExpression();
        //complexExpressionFinal.addExpression(simpleExpression1);
        //String json = JacksonUtil.toJson(complexExpressionFinal);
        String json = JacksonUtil.toJson(simpleExpression1);
        System.out.println(json);

    }
    public static void test1_1(){
        String json = "{\"exprType\":\"simple\",\"exprType\":\"simple\",\"var\":\"age\",\"operator\":\">\"," +
                "\"operand\":\"20\"}";
        Expression expression = JacksonUtil.toObject(json, Expression.class);
        System.out.println(expression);
    }

    public static void test2(){
        SimpleExpression simpleExpression1 = new SimpleExpression("age", ">", "20");

        SimpleExpression simpleExpression2 = new SimpleExpression("gender", "=", "male");
        SimpleExpression simpleExpression3 = new SimpleExpression("education", "not in", "小学，初中");

        ComplexExpression complexExpressionFinal = new ComplexExpression();
        complexExpressionFinal.addExpression(simpleExpression1);
        complexExpressionFinal.addExpression(simpleExpression2);
        complexExpressionFinal.addExpression(simpleExpression3);
        complexExpressionFinal.setLink("and");
        String json = JacksonUtil.toJson(complexExpressionFinal);
        System.out.println(json);
    }
    public static void test2_1(){
        String json = "{\"exprType\":\"complex\",\"exprType\":\"complex\",\"expressions\":[{\"exprType\":\"simple\"," +
                "\"exprType\":\"simple\",\"var\":\"age\",\"operator\":\">\",\"operand\":\"20\"}," +
                "{\"exprType\":\"simple\",\"exprType\":\"simple\",\"var\":\"gender\",\"operator\":\"=\"," +
                "\"operand\":\"male\"},{\"exprType\":\"simple\",\"exprType\":\"simple\",\"var\":\"education\"," +
                "\"operator\":\"not in\",\"operand\":\"小学，初中\"}],\"link\":\"and\"}";
        Expression expression = JacksonUtil.toObject(json, Expression.class);
        System.out.println(expression);
    }

    public static void test3(){
        SimpleExpression simpleExpression1 = new SimpleExpression("age", ">", "20");

        //SimpleExpression simpleExpression2 = new SimpleExpression("gender", "=", "male");
        SimpleExpression simpleExpression2_1 = new SimpleExpression("gender", "=", "male");
        SimpleExpression simpleExpression2_2 = new SimpleExpression("work", "in", "公务员，技术人员，工程师");
        ComplexExpression complexExpression2 = new ComplexExpression();
        complexExpression2.addExpression(simpleExpression2_1);
        complexExpression2.addExpression(simpleExpression2_2);
        complexExpression2.setLink("or");
        SimpleExpression simpleExpression3 = new SimpleExpression("education", "not in", "小学，初中");

        ComplexExpression complexExpressionFinal = new ComplexExpression();
        complexExpressionFinal.addExpression(simpleExpression1);
        complexExpressionFinal.addExpression(complexExpression2);
        complexExpressionFinal.addExpression(simpleExpression3);
        complexExpressionFinal.setLink("and");
        String json = JacksonUtil.toJson(complexExpressionFinal);
        System.out.println(json);
    }

    public static void test3_1(){
        String expr = "{\"exprType\":\"complex\",\"exprType\":\"complex\",\"expressions\":[{\"exprType\":\"simple\"," +
                "\"exprType\":\"simple\",\"var\":\"age\",\"operator\":\">\",\"operand\":\"20\"}," +
                "{\"exprType\":\"complex\",\"exprType\":\"complex\",\"expressions\":[{\"exprType\":\"simple\"," +
                "\"exprType\":\"simple\",\"var\":\"gender\",\"operator\":\"=\",\"operand\":\"male\"}," +
                "{\"exprType\":\"simple\",\"exprType\":\"simple\",\"var\":\"work\",\"operator\":\"in\"," +
                "\"operand\":\"公务员，技术人员，工程师\"}],\"link\":\"or\"},{\"exprType\":\"simple\",\"exprType\":\"simple\"," +
                "\"var\":\"education\",\"operator\":\"not in\",\"operand\":\"小学，初中\"}],\"link\":\"and\"}";
        Expression complexExpression = JacksonUtil.toObject(expr, Expression.class);
        System.out.println(complexExpression);
    }

}

