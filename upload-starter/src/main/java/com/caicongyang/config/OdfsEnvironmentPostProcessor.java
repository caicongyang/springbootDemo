package com.caicongyang.config;


import com.caicongyang.client.config.UploadConfig;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Properties;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;

public class OdfsEnvironmentPostProcessor implements EnvironmentPostProcessor {


    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        try {
           processEnv(environment, new OdfsProperties(), "spring.odfs", UploadConfig.getProperties());
        } catch (Exception e) {
            throw new RuntimeException("初始化 ODFS 环境配置出错", e);
        }
    }


    public static Map<String, String> propertiesMap(Object object, String prefix) throws InvocationTargetException, IllegalAccessException, InstantiationException {
        Map<String, String> properties = new HashMap();
        Deque<String> deque = new LinkedList();
        Map<String, Object> prefixMap = new HashMap();
        putField(object, prefix, deque, prefixMap);

        while(true) {
            while(!deque.isEmpty()) {
                String key = (String)deque.pop();
                Object value = prefixMap.get(key);
                if (value != null && value.getClass().isMemberClass()) {
                    putField(value, key, deque, prefixMap);
                } else {
                    properties.put(key, null == value ? null : value.toString());
                }
            }

            return properties;
        }
    }

    private static void putField(Object object, String prefix, Deque<String> deque, Map<String, Object> prefixMap) throws InvocationTargetException, IllegalAccessException, InstantiationException {
        PropertyDescriptor[] propertyDescriptors = BeanUtils.getPropertyDescriptors(object.getClass());
        PropertyDescriptor[] var5 = propertyDescriptors;
        int var6 = propertyDescriptors.length;

        for(int var7 = 0; var7 < var6; ++var7) {
            PropertyDescriptor propertyDescriptor = var5[var7];
            Object value = propertyDescriptor.getReadMethod().invoke(object);
            if (!(value instanceof Class)) {
                Class<?> propertyType = propertyDescriptor.getPropertyType();
                if (propertyType.isMemberClass() && value == null) {
                    value = propertyType.newInstance();
                }

                String key = CamelCaseKeyUtil.field2key(propertyDescriptor.getName(), prefix);
                prefixMap.put(key, value);
                deque.push(key);
            }
        }

    }

    public static void processEnv(Environment environment, Object obj, String prefix, Properties properties) throws Exception {
        Map<String, String> stringStringMap = propertiesMap(obj, prefix);
        stringStringMap.forEach((k, v) -> {
            String property = environment.getProperty(k);
            if (StringUtils.isNotBlank(property)) {
                v = property;
            }

            if (v != null) {
                properties.setProperty(k, v);
            }

        });
    }


}
