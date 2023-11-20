package com.caicongyang.stock.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.StringWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author caicongyang
 * @version $Id: JsonUtils.java, v 0.1 2015年7月17日 上午11:19:30 caicongyang Exp $
 */
public class JacksonUtil {

    public static final Logger LOGGER = LoggerFactory.getLogger(JacksonUtil.class);
    private static final ObjectMapper mapper = new ObjectMapper();

    /**
     * 对象转json
     *
     * @param object
     * @return
     */
    public static String jsonFromObject(Object object) {
        StringWriter writer = new StringWriter();
        try {
            mapper.writeValue(writer, object);
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            LOGGER.error("Unable to serialize to json: " + object, e);
            return null;
        }
        return writer.toString();
    }

    /**
     * json转对象
     *
     * @param json
     * @param klass
     * @return
     */
    public static <T> T objectFromJson(String json, TypeReference<T> klass) {
        T object = null;

        //设置输入时忽略JSON字符串中存在而Java对象实际没有的属性
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        try {
            object = mapper.readValue(json, klass);
        } catch (JsonProcessingException e) {
            LOGGER.error("Unable to serialize to json: {}", json, e);
        }
        return object;
    }

    /**
     * json转对象
     *
     * @param json
     * @param klass
     * @return
     */
    public static <T> T objectFromJson(String json, Class<T> klass) {
        T object = null;
        //设置输入时忽略JSON字符串中存在而Java对象实际没有的属性
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        try {
            object = mapper.readValue(json, klass);
        } catch (JsonProcessingException e) {
            LOGGER.error("Unable to serialize to json: {}", json, e);
        }
        return object;
    }

}