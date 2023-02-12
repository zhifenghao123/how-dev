package com.howdev.manage.util;

import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * ObjectFieldUtil class
 *
 * @author haozhifeng
 * @date 2023/02/12
 */
public class ObjectFieldUtil {
    private static final Logger logger = LoggerFactory.getLogger(ObjectFieldUtil.class);

    private ObjectFieldUtil() {
        // no need to do.
    }

    /**
     * 找到object对象属性fieldName的值
     *
     * @param object object
     * @param fieldName 属性名
     * @return:
     * @author: haozhifeng
     */
    public static <T> Object getFieldValueWithException(T object, String fieldName) {

        // 获取所有的字段, 包含私有字段
        Field[] fields = getAllField(object.getClass());

        // 遍历所有的字段
        for (Field field : fields) {

            // 打开私有访问权限, 用于访问私有变量的值
            field.setAccessible(true);
            if (fieldName.equals(field.getName())) {
                try {
                    Object fieldObjectValue = field.get(object);
                    return fieldObjectValue;
                } catch (Exception e) {
                    return null;
                }
            }
        }
        return null;
    }

    /**
     * 获取 class 的所有field, 包括父类
     *
     * @param cls 目标类
     * @return 所有的参数包括父类
     */
    public static Field[] getAllField(Class<?> cls) {
        Field[] allFields = new Field[0];
        for (Class<?> clsTemp = cls; clsTemp != Object.class; clsTemp = clsTemp.getSuperclass()) {
            Field[] currentFields = clsTemp.getDeclaredFields();
            allFields = ArrayUtils.addAll(allFields, currentFields);
        }
        return allFields;
    }

    public static List<Field> getAllFields(Class<?> clazz) {
        List<Field> fields = new ArrayList(Arrays.asList(clazz.getDeclaredFields()));
        Class superclass = clazz.getSuperclass();
        if (superclass != null) {
            fields.addAll(getAllFields(superclass));
        }

        return fields;
    }

    /**
     * 设置对象的属性为空
     * @param t 目标类
     */
    public static <T> void setObjectFieldsNull(T t) {
        getAllFields(t.getClass()).stream().filter(field -> !Modifier.isStatic(field.getModifiers()))
                .filter(field -> !field.getType().isPrimitive())
                .forEach(field -> {
                    try {
                        field.setAccessible(true);
                        field.set(t, null);
                    } catch (Exception e) {
                        logger.error("set object:{} field: {} null error: {}", t, field.getName(), e);
                    }
                });
    }

}
