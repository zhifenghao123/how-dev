package com.howdev.framework.sqlcalculate.jdbc.cacheloader;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * SqlServiceConfigCacheLoader class
 *
 * @author haozhifeng
 * @date 2023/12/12
 */
public class SqlServiceConfigCacheLoader {
    private static final Logger LOGGER = LoggerFactory.getLogger(SqlServiceConfigCacheLoader.class);

    private static final String SQL_VAR_CONFIG_PATH = "sqlcalculate/sql_calc_var.properties";

    private static  Map<String, List<String>> loadConfig(String configPath) throws IOException {
        Map<String, List<String>> configMap = new HashMap<>();

        Resource resource = new ClassPathResource(configPath);
        Properties props = new Properties();
        InputStream is = resource.getInputStream();
        try {
            BufferedReader bf = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            props.load(bf);
        } finally {
            is.close();
        }
        Enumeration<String> enumeration = (Enumeration<String>) props.propertyNames();
        while (enumeration.hasMoreElements()) {
            String key = enumeration.nextElement();
            String value = props.getProperty(key);
            if (StringUtils.isNotBlank(value)) {
                List<String> list = Arrays.asList(value.split(";"));
                configMap.put(key, list);
            }
        }
        LOGGER.info("SqlServiceConfigCacheLoader loadConfig succeed");
        return configMap;
    }

    public static Map<String, List<String>> loadSqlVarConfig() {
        Map<String, List<String>> sqlValConfig = null;
        try {
            sqlValConfig = loadConfig(SQL_VAR_CONFIG_PATH);
        } catch (IOException e) {
            LOGGER.info("SqlServiceConfigCacheLoader loadConfig error");
            e.printStackTrace();
        }
        return sqlValConfig;
    }

    public static List<String> loadSqlVarConfig(String key) {
        Map<String, List<String>> sqlValConfig = loadSqlVarConfig();
        if (sqlValConfig == null) {
            return null;
        }
        return sqlValConfig.get(key);
    }
}
