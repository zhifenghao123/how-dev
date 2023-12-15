package com.howdev.common.util;

import org.apache.commons.collections4.MapUtils;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.lang3.StringUtils;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * CsvUtil class
 *
 * @author haozhifeng
 * @date 2023/12/15
 */
public class CsvUtil {

    /**
     * 读取csv文件
     *
     * @param fileName fileName
     * @return:
     * @author: haozhifeng
     */
    public static List<Map<String, Object>> readAndParseToMapList(String fileName) throws Exception {
        List<Map<String, Object>> data = new ArrayList<>();
        try (CSVParser parser = new CSVParser(new FileReader(fileName), CSVFormat.DEFAULT.withFirstRecordAsHeader())) {
            Map<String, Integer> headerMap = parser.getHeaderMap();
            if (MapUtils.isEmpty(headerMap)) {
                throw new Exception("CSV file header is empty");
            }
            Set<String> headers = headerMap.keySet();
            List<CSVRecord> records = parser.getRecords();
            for (int i = 1; i < records.size(); i++) {
                CSVRecord record = records.get(i);
                Map<String, Object> row = new LinkedHashMap<>();
                for (int j = 0; j < headers.size(); j++) {
                    String header = headers.toArray(new String[0])[j];
                    row.put(header, record.get(j));
                }
                data.add(row);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("Error reading CSV file", e);
        }
        return data;
    }

    /**
     * 读取CSV文件，解析为对象集合。
     * （注意，传入的泛型类T对应的实际class的属性要和csv的标题头一致，否则会丢失对应的属性）
     *
     * @param filePath filePath
     * @param clazz clazz
     * @return:
     * @author: haozhifeng
     */
    public static <T> List<T> readAndParseToObjectList(String filePath, Class<T> clazz){
        List<Map<String, Object>> csvData = null;
        try {
            csvData = readAndParseToMapList(filePath);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (csvData == null) {
            return null;
        }
        String csvDataJsonArrStr = JacksonUtil.toJson(csvData);
        if (StringUtils.isEmpty(csvDataJsonArrStr)) {
            return null;
        }
        return JacksonUtil.toObject(csvDataJsonArrStr, List.class, clazz);
    }

}
