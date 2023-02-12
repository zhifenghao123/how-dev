package com.howdev.manage.util;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.StringWriter;
import java.text.SimpleDateFormat;

/**
 * JacksonUtil class
 *
 * @author haozhifeng
 * @date 2023/02/10
 */
public class JacksonUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(JacksonUtil.class);
    private static final String DATE_PATTERN = "yyyyMMddHHmmss";
    private static final JsonFactory JSON_FACTORY = new JsonFactory();
    private static final ObjectMapper OBJECT_MAPPER = createObjectMapper();
    private static final ObjectMapper OBJECT_IGNORE_MAPPER = createObjectMapper();
    private static final SimpleFilterProvider FILTER_PROVIDER = new SimpleFilterProvider();
    private static final String DEFAULT_FILTER_ID = "filterDataGroup";

    private JacksonUtil() {
        // no need to do.
    }

    /**
     * 创建一个自定义的JSON ObjectMapper
     */
    private static ObjectMapper createObjectMapper() {

        ObjectMapper objectMapper = new ObjectMapper();

        objectMapper.setDateFormat(new SimpleDateFormat(DATE_PATTERN));
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.configure(SerializationFeature.INDENT_OUTPUT, false);
        objectMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_CONTROL_CHARS, true);
        objectMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
        objectMapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);

        return objectMapper;
    }

    public static ObjectWriter getWriter() {
        return createObjectMapper().writer();
    }

    public static ObjectReader getReader() {
        return createObjectMapper().reader();
    }

    /**
     * 将对象转换为JSON字符串
     */
    public static <T> String toJson(T value) {
        if (value == null) {
            return null;
        }
        StringWriter sw = new StringWriter();
        JsonGenerator gen = null;
        try {
            gen = JSON_FACTORY.createGenerator(sw);
            OBJECT_MAPPER.writeValue(gen, value);
            return sw.toString();
        } catch (IOException e) {
            LOGGER.error("object to json exception!", e);
        } finally {
            if (gen != null) {
                try {
                    gen.close();
                } catch (IOException e) {
                    LOGGER.warn("Exception occurred when closing JSON generator!", e);
                }
            }
        }
        return null;
    }

    /**
     * 将对象转换为JSON字符串,动态过滤对应的属性
     */
    public static <T> String toJsonFilter(T value, String... propertyArray) {
        if (value == null) {
            return null;
        }
        StringWriter sw = new StringWriter();
        JsonGenerator gen = null;
        try {
            gen = JSON_FACTORY.createGenerator(sw);
            FILTER_PROVIDER.addFilter(DEFAULT_FILTER_ID, SimpleBeanPropertyFilter.serializeAllExcept(propertyArray));
            OBJECT_IGNORE_MAPPER.setFilterProvider(FILTER_PROVIDER);
            OBJECT_IGNORE_MAPPER.writeValue(gen, value);
            return sw.toString();
        } catch (IOException e) {
            LOGGER.error("object to json exception!", e);
        } finally {
            if (gen != null) {
                try {
                    gen.close();
                } catch (IOException e) {
                    LOGGER.warn("Exception occurred when closing JSON generator!", e);
                }
            }
        }
        return null;
    }

    public static <T> T fromJson(String json, Class<T> clazz) throws JsonProcessingException {
        return getReader().forType(clazz).readValue(json);
    }

    /**
     * 将JSON字符串转换为指定对象
     */
    public static <T> T toObject(String jsonString, Class<T> valueType, Class<?>... itemTypes) {
        if (StringUtils.isEmpty(jsonString)) {
            return null;
        }
        try {
            if (itemTypes.length == 0) {
                // 非集合类型
                return OBJECT_MAPPER.readValue(jsonString, valueType);
            } else {
                // 集合类型, 如List,Map
                JavaType javaType =
                        OBJECT_MAPPER.getTypeFactory().constructParametrizedType(valueType, valueType, itemTypes);
                return OBJECT_MAPPER.readValue(jsonString, javaType);
            }
        } catch (Exception e) {
            LOGGER.error("json to object exception!", e);
            throw new RuntimeException("json to object exception:" + jsonString);
        }
    }

    /**
     * 自定义TypeReference，解决复杂嵌套结构的转换
     */
    @SuppressWarnings("unchecked")
    public static <T> T toObject(String jsonString, TypeReference<T> type) {
        if (StringUtils.isEmpty(jsonString) && type == null) {
            return null;
        }
        try {
            return OBJECT_MAPPER.readValue(jsonString, type);
        } catch (Exception e) {
            LOGGER.error("json to object exception!", e);
            throw new RuntimeException("json to object exception:" + jsonString);
        }
    }
}
