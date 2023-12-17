package com.howdev.common.util;

import com.howdev.common.datamodel.util.ProductDTO;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import java.util.List;
import java.util.Map;

/** 
* CsvUtil Tester. 
* 
* @author <Authors name> 
* @since <pre>12/15/2023</pre> 
* @version 1.0 
*/ 
public class CsvUtilTest extends TestCase { 
public CsvUtilTest(String name) { 
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
* Method: readAndParseToMapList(String fileName) 
* 
*/ 
public void testReadAndParseToMapList() throws Exception {
    List<Map<String, Object>> mapList = CsvUtil.readAndParseToMapList("./how-dev-common/data_dir/product_view.csv");
    mapList.forEach(System.out::println);
} 


/** 
* 
* Method: readAndParseToObjectList(String filePath, Class<T> clazz) 
* 
*/ 
public void testReadAndParseToObjectList() throws Exception {
    List<ProductDTO> productDTOs = CsvUtil.readAndParseToObjectList("./how-dev-common/data_dir/product_view.csv", ProductDTO.class);
    productDTOs.forEach(System.out::println);
} 


public static Test suite() { 
return new TestSuite(CsvUtilTest.class); 
} 
} 
