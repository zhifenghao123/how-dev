package com.howdev.framework.sqlcalculate.example.sqlservice;

import com.howdev.framework.sqlcalculate.example.dto.ProductDTO;
import junit.framework.Test;
import junit.framework.TestSuite; 
import junit.framework.TestCase;
import org.apache.commons.lang3.time.DateUtils;

import java.time.LocalDateTime;
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
    productDTO1.setDescription("5G手机一部");
    productDTO1.setPrice("2000.00");
    productDTO1.setCategoryId(2L);
    productDTO1.setCreated(DateUtils.parseDate("2023-11-07 12:22:39", "yyyy-MM-dd HH:mm:ss"));
    productDTO1.setUpdated(DateUtils.parseDate("2023-11-07 12:22:39", "yyyy-MM-dd HH:mm:ss"));

    ProductDTO productDTO2 = new ProductDTO();
    productDTO2.setId(2L);
    productDTO2.setProductId(2L);
    productDTO2.setName("海尔洗衣机");
    productDTO2.setDescription("智能洗衣机");
    productDTO2.setPrice("3600.00");
    productDTO2.setCategoryId(3L);
    productDTO2.setCreated(DateUtils.parseDate("2023-11-07 12:22:39", "yyyy-MM-dd HH:mm:ss"));
    productDTO2.setUpdated(DateUtils.parseDate("2023-11-07 12:22:39", "yyyy-MM-dd HH:mm:ss"));

    productDTOList.add(productDTO1);
    productDTOList.add(productDTO2);
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
