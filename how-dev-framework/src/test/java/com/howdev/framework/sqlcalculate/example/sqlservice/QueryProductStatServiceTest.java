package com.howdev.framework.sqlcalculate.example.sqlservice;

import com.howdev.framework.sqlcalculate.example.dto.ProductDTO;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.apache.commons.lang3.time.DateUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/** 
* QueryProductStatService Tester. 
* 
* @author <Authors name> 
* @since <pre>12/14/2023</pre> 
* @version 1.0 
*/ 
public class QueryProductStatServiceTest extends TestCase { 
public QueryProductStatServiceTest(String name) { 
super(name); 
} 

public void setUp() throws Exception { 
super.setUp(); 
} 

public void tearDown() throws Exception { 
super.tearDown(); 
} 

/** 
* 
* Method: process(List<ProductDTO> productDTOList) 
* 
*/ 
public void testProcess() throws Exception {
    QueryProductStatService queryProductStatService = new QueryProductStatService();
    List<ProductDTO> productDTOList = new ArrayList<>();
    ProductDTO productDTO1 = new ProductDTO();
    productDTO1.setId(1L);
    productDTO1.setProductId(1L);
    productDTO1.setName("华为Nova 7 5G");
    productDTO1.setDescription("华为5G智能手机，麒麟芯片，值得拥有");
    productDTO1.setPrice("2000.00");
    productDTO1.setCreated(DateUtils.parseDate("2021-11-07 12:22:39", "yyyy-MM-dd HH:mm:ss"));
    productDTO1.setUpdated(DateUtils.parseDate("2021-11-07 12:22:39", "yyyy-MM-dd HH:mm:ss"));

    ProductDTO productDTO2 = new ProductDTO();
    productDTO2.setId(2L);
    productDTO2.setProductId(2L);
    productDTO2.setName("海尔洗衣机");
    productDTO2.setDescription("海尔出品，全自动智能洗衣机");
    productDTO2.setPrice("3600.00");
    productDTO2.setCreated(DateUtils.parseDate("2022-01-07 12:22:39", "yyyy-MM-dd HH:mm:ss"));
    productDTO2.setUpdated(DateUtils.parseDate("2022-01-07 12:22:39", "yyyy-MM-dd HH:mm:ss"));

    ProductDTO productDTO3 = new ProductDTO();
    productDTO3.setId(3L);
    productDTO3.setProductId(3L);
    productDTO3.setName("纯甄牛奶");
    productDTO3.setDescription("好牛奶，助力你的健康成长");
    productDTO3.setPrice("50.00");
    productDTO3.setCreated(DateUtils.parseDate("2022-10-07 12:22:39", "yyyy-MM-dd HH:mm:ss"));
    productDTO3.setUpdated(DateUtils.parseDate("2022-10-07 12:22:39", "yyyy-MM-dd HH:mm:ss"));

    ProductDTO productDTO4 = new ProductDTO();
    productDTO4.setId(4L);
    productDTO4.setProductId(4L);
    productDTO4.setName("陕西富士苹果");
    productDTO4.setDescription("出自陕西的好苹果，富含各种维生素，唯甜，水多");
    productDTO4.setPrice("3.25");
    productDTO4.setCreated(DateUtils.parseDate("2022-12-31 12:22:39", "yyyy-MM-dd HH:mm:ss"));
    productDTO4.setUpdated(DateUtils.parseDate("2022-12-31 12:22:39", "yyyy-MM-dd HH:mm:ss"));

    ProductDTO productDTO5 = new ProductDTO();
    productDTO5.setId(5L);
    productDTO5.setProductId(5L);
    productDTO5.setName("黄心土豆");
    productDTO5.setDescription("买土豆就选黄心土豆");
    productDTO5.setPrice("0.98");
    productDTO5.setCreated(DateUtils.parseDate("2023-03-07 12:22:39", "yyyy-MM-dd HH:mm:ss"));
    productDTO5.setUpdated(DateUtils.parseDate("2023-03-07 12:22:39", "yyyy-MM-dd HH:mm:ss"));

    ProductDTO productDTO6 = new ProductDTO();
    productDTO6.setId(6L);
    productDTO6.setProductId(6L);
    productDTO6.setName("MacBook Pro 13");
    productDTO6.setDescription("苹果的高端笔记本，成功人士的必配电脑");
    productDTO6.setPrice("8000.00");
    productDTO6.setCreated(DateUtils.parseDate("2023-07-07 12:22:39", "yyyy-MM-dd HH:mm:ss"));
    productDTO6.setUpdated(DateUtils.parseDate("2023-07-07 12:22:39", "yyyy-MM-dd HH:mm:ss"));

    productDTOList.add(productDTO1);
    productDTOList.add(productDTO2);
    productDTOList.add(productDTO3);
    productDTOList.add(productDTO4);
    productDTOList.add(productDTO5);
    productDTOList.add(productDTO6);
    Map<String, Object> processResult = queryProductStatService.process(productDTOList);
    System.out.println(processResult);
} 

/** 
* 
* Method: getSqlServiceKey() 
* 
*/ 
public void testGetSqlServiceKey() throws Exception { 
//TODO: Test goes here... 
} 

/** 
* 
* Method: getUsedDbEngine() 
* 
*/ 
public void testGetUsedDbEngine() throws Exception { 
//TODO: Test goes here... 
} 

/** 
* 
* Method: getUdfRegistrar() 
* 
*/ 
public void testGetUdfRegistrar() throws Exception { 
//TODO: Test goes here... 
} 


/** 
* 
* Method: batchConvert(List<ProductDTO> productDTOList) 
* 
*/ 
public void testBatchConvert() throws Exception { 
//TODO: Test goes here... 
/* 
try { 
   Method method = QueryProductStatService.getClass().getMethod("batchConvert", List<ProductDTO>.class); 
   method.setAccessible(true); 
   method.invoke(<Object>, <Parameters>); 
} catch(NoSuchMethodException e) { 
} catch(IllegalAccessException e) { 
} catch(InvocationTargetException e) { 
} 
*/ 
} 


public static Test suite() { 
return new TestSuite(QueryProductStatServiceTest.class); 
} 
} 
