package com.caicongyang.cache;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * @author caicongyang
 * @version $Id: JsonUtils.java, v 0.1 2015年7月17日 上午11:19:30 caicongyang Exp $
 */
public class JacksonUtils {

    public static final Logger LOGGER = LoggerFactory.getLogger(JacksonUtils.class);
    private final static ObjectMapper mapper = new ObjectMapper();


    static {
        mapper.configure(MapperFeature.USE_ANNOTATIONS, false);
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        // 此项必须配置，否则会报java.lang.ClassCastException: java.util.LinkedHashMap cannot be cast to XXX
        mapper.activateDefaultTyping(
                LaissezFaireSubTypeValidator.instance,
                ObjectMapper.DefaultTyping.NON_FINAL,
                JsonTypeInfo.As.WRAPPER_ARRAY);

    }

    /**
     * 对象转json
     *
     * @param object
     * @return
     */
    public static String jsonFromObject(Object object) {
        try {
            return mapper.writeValueAsString(object);
        } catch (IOException e) {
            LOGGER.error("Unable to serialize to json: " + object, e);
            return null;
        }
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

        try {
            object = mapper.readValue(json, klass);
        } catch (JsonProcessingException e) {
            LOGGER.error("Unable to serialize to json: {}", json, e);
        }
        return object;
    }

}