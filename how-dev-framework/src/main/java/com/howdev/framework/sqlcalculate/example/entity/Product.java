package com.howdev.framework.sqlcalculate.example.entity;

import com.howdev.framework.sqlcalculate.jdbc.table.EntityTableConvertible;
import com.howdev.framework.sqlcalculate.jdbc.table.Table;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Product class
 *
 * @author haozhifeng
 * @date 2023/12/13
 */
@Table(tableName = Product.TABLE_NAME, createTableSql = Product.CREATE_TABLE_SQL)
public class Product implements EntityTableConvertible {
    public static final String TABLE_NAME = "Product";
    public static final String CREATE_TABLE_SQL =
            "CREATE TABLE " + TABLE_NAME +" ("
            + "  `id` bigint(20),"
            + "  `product_id` bigint(20),"
            + "  `name` varchar(128),"
            + "  `description` varchar(128),"
            + "  `price` decimal(10,2),"
            + "  `categoryId` bigint(20),"
            + "  `created` datetime,"
            + "  `updated` datetime"
            + ");";
    private Long id;
    private Long productId;
    private String name;
    private String description;
    private String price;
    private Long categoryId;
    private Date created;
    private Date updated;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public Date getUpdated() {
        return updated;
    }

    public void setUpdated(Date updated) {
        this.updated = updated;
    }

    public String convertToInsertVarcharValue(Object obj) {
        if (obj == null) {
            return null;
        }else if (obj instanceof String) {
            return "'" + obj + "'";
        } else if (obj instanceof Date) {
            return "'" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(obj) + "'";
        } else if (obj instanceof Boolean) {
            if ((Boolean) obj) {
                return "1";
            } else {
                return "0";
            }
        } else {
            return obj.toString();
        }
    }

    @Override
    public String convertToInsertSql() {
        return "insert into " + TABLE_NAME
                + " values ("
                + convertToInsertVarcharValue(id) + ","
                + convertToInsertVarcharValue(productId) + ","
                + convertToInsertVarcharValue(name) + ","
                + convertToInsertVarcharValue(description) + ","
                + convertToInsertVarcharValue(price) + ","
                + convertToInsertVarcharValue(categoryId) + ","
                + convertToInsertVarcharValue(created) + ","
                + convertToInsertVarcharValue(updated) + ")";
    }
}
