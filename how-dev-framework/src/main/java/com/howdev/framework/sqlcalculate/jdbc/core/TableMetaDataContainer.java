package com.howdev.framework.sqlcalculate.jdbc.core;

import com.howdev.framework.sqlcalculate.jdbc.table.EntityTableConvertible;
import com.howdev.framework.sqlcalculate.jdbc.table.Table;
import org.apache.commons.collections4.MapUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * TableMetaDataContainer class
 *
 * @author haozhifeng
 * @date 2023/12/13
 */
public class TableMetaDataContainer {
    /**
     * 注册的TableMetaData
     */
    private Map<String, TableMetaData> tableMetaDataMap = new HashMap<>();

    /**
     * 获取注册的注册的TableMetaData集合
     *
     * @param
     * @return:
     * @author: haozhifeng
     */
    public List<TableMetaData> getRegisteredTableMetaDataList() {
        if(MapUtils.isEmpty(tableMetaDataMap)) {
            return null;
        }
        return new ArrayList<>(tableMetaDataMap.values());
    }

    /**
     * 向容器中添加TableMetaData
     *
     * @param tableEntityClass 实体类
     * @param dataList 类数据集合
     * @return: 添加的结果：true表示添加成功，false表示添加失败
     * @author: haozhifeng
     */
    public <T extends EntityTableConvertible> boolean addTableMetaData(Class<T> tableEntityClass,
                                                                       List<T> dataList) {
        if (tableEntityClass == null) {
            return false;
        }
        Table table = tableEntityClass.getAnnotation(Table.class);
        if (table == null) {
            return false;
        }
        String tableName = table.tableName();
        String createTableSql = table.createTableSql();
        List<String> insertDataSqlList = new ArrayList<>();
        dataList.forEach(data -> {
            insertDataSqlList.add(data.convertToInsertSql());
        });

        TableMetaData existedTableMetaData = tableMetaDataMap.get(tableName);
        if (null == existedTableMetaData) {
            TableMetaData tableMetaData = new TableMetaData();
            tableMetaData.setTableName(tableName);
            tableMetaData.setCreateTableSql(createTableSql);
            tableMetaData.setInsertDataSqlList(insertDataSqlList);
            tableMetaDataMap.put(tableName, tableMetaData);
        } else {
            existedTableMetaData.getInsertDataSqlList().addAll(insertDataSqlList);
        }
        return true;
    }


}
