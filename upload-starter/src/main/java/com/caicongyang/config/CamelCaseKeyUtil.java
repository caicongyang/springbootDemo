package com.caicongyang.config;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import org.springframework.core.env.Environment;

/**
 * @Description 移除occ兼容改造
 * @Author 姚仲杰#80998699
 * @Date 2021/6/7 15:19
 */
public class CamelCaseKeyUtil {
    
    public static String field2key(String fieldName,String prefix) {
        
        char[] fieldArrays = fieldName.toCharArray();
        StringBuilder buffer = new StringBuilder(".");
        for(char c : fieldArrays) {
            if(Character.isUpperCase(c)) {
                buffer.append("-");
                buffer.append(Character.toLowerCase(c));
            } else {
                buffer.append(c);
            }
        }
        return prefix+buffer.toString();
    }
    public static Map<String,String> propertiesClass2Map(Class<?> clazz, Environment environment,String prefix){
        Map<String,String> map=new HashMap<>();
        try {
            Object obj = clazz.newInstance();
            Field[] declaredFields = clazz.getDeclaredFields();
            for (Field declaredField : declaredFields) {
                declaredField.setAccessible(true);
                String key = CamelCaseKeyUtil.field2key(declaredField.getName(),prefix);
                Object value = declaredField.get(obj);
                String property = environment.getProperty(key);
                if (property!=null){
                    value=property;
                }
                if (value!=null) {
                    Class<?> type = declaredField.getType();
                    if(type.isAssignableFrom(String.class)){
                        map.put(key,(String) value);
                    }
                    if (type.isAssignableFrom(Integer.class)||type.isAssignableFrom(Boolean.class)){
                        map.put(key,String.valueOf(value));
                    }
                }
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }
        return map;
    }
}
