package com.howdev.framework.sqlcalculate.example.sqlservice;

import com.howdev.framework.sqlcalculate.example.dto.ProductDTO;
import com.howdev.framework.sqlcalculate.example.entity.Product;
import com.howdev.framework.sqlcalculate.example.udf.sqlite.SqliteNumToStrFunction;
import com.howdev.framework.sqlcalculate.jdbc.core.TableMetaDataContainer;
import com.howdev.framework.sqlcalculate.jdbc.enumeration.DbEngineEnum;
import com.howdev.framework.sqlcalculate.jdbc.sqlservice.SqlCalculate;
import com.howdev.framework.sqlcalculate.jdbc.udf.UdfRegistrar;
import org.apache.commons.collections4.CollectionUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * QueryProductStatService class
 *
 * @author haozhifeng
 * @date 2023/12/13
 */
public class QueryProductStatService extends SqlCalculate {
    private static final String SQL_SERVICE_KEY = "QueryProductStatService";

    public Map<String, Object> process(List<ProductDTO> productDTOList) {
        List<Product> products = batchConvert(productDTOList);

        TableMetaDataContainer tableMetaDataContainer = new TableMetaDataContainer();
        tableMetaDataContainer.addTableMetaData(Product.class, products);

        Map<String, Object> userVariables = new HashMap<>();
        userVariables.put("startTime", "2023-12-15 00:00:00");

        Map<String, Object> map = calculateSql(tableMetaDataContainer, userVariables);
        return map;
    }

    private List<Product> batchConvert(List<ProductDTO> productDTOList){
        if (CollectionUtils.isEmpty(productDTOList)) {
            return null;
        }
        List<Product> products = productDTOList.stream().map(productDTO -> {
            Product product = new Product();
            product.setId(productDTO.getId());
            product.setProductId(productDTO.getProductId());
            product.setName(productDTO.getName());
            product.setPrice(productDTO.getPrice());
            product.setCreated(productDTO.getCreated());
            product.setUpdated(productDTO.getUpdated());
            return product;
        }).collect(Collectors.toList());
        return products;
    }

    @Override
    public String getSqlServiceKey() {
        return SQL_SERVICE_KEY;
    }

    @Override
    public DbEngineEnum getUsedDbEngine() {
        return DbEngineEnum.SQLITE;
    }

    @Override
    public UdfRegistrar getUdfRegistrar() {
        UdfRegistrar udfRegistrar = new UdfRegistrar();
        udfRegistrar.registerUdf(new SqliteNumToStrFunction());
        return udfRegistrar;
    }
}
