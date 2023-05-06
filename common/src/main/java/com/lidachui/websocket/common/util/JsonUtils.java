package com.lidachui.websocket.common.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lidachui.websocket.common.constants.NumberConstants;

import java.io.IOException;
import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * JsonUtils
 *
 * @Author lihuijie
 * @Description:
 * @SINCE 2023/4/10 23:14
 */
public class JsonUtils {

    private static final ObjectMapper objectMapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    /**
     * 将json字符串转换成对应的Java对象
     *
     * @param json  待转换的json字符串
     * @param clazz 要转换的目标Java对象的类型
     * @return 转换后的Java对象
     * @throws IOException 当读取json字符串失败时抛出异常
     */
    public static <T> T fromJson(String json, Class<T> clazz) throws IOException {
        if (clazz.getTypeParameters().length > NumberConstants.ZERO) {
            // 处理多层级泛型实体类型
            return objectMapper.readValue(json, objectMapper.getTypeFactory().constructType(clazz));
        } else {
            // 处理普通类型
            return objectMapper.readValue(json, clazz);
        }
    }

    /**
     * 将Java对象转换成json字符串
     *
     * @param object 待转换的Java对象
     * @return 转换后的json字符串
     * @throws JsonProcessingException 当Java对象转换成json字符串失败时抛出异常
     */
    public static String toJson(Object object) {
        try {
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 将Java对象的属性值转换成json字符串，并将其设置回Java对象中
     *
     * @param object 要转换的Java对象
     * @param getter 获取Java对象属性值的函数
     * @param setter 设置Java对象属性值的操作
     * @param <T>    Java对象的类型
     * @param <V>    属性值的类型
     * @return 转换后的json字符串
     */
    public static <T, V> String convertToJsonString(T object, Function<T, V> getter, BiConsumer<T, V> setter) {
        V oldValue = getter.apply(object);
        // 当属性发生变化时执行操作
        setter.accept(object, (V) toJson(oldValue));
        return toJson(oldValue);
    }

}


