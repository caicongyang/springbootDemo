package com.caicongyang.basic;

import com.caicongyang.utils.DateUtils;
import com.caicongyang.utils.ReflectUtils;
import com.google.common.collect.Maps;

import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author bo.wu
 * @CreateDate 2014年7月23日
 */
public final class ValueConverterRegistry {
    private static ValueConverterRegistry instance = new ValueConverterRegistry();
    
    public static ValueConverterRegistry getInstance() {
        return instance;
    }
    
    private Map<Type, ValueConverter<?>> converters = new HashMap<Type, ValueConverter<?>>();
    
    private ValueConverterRegistry() {
        register(Integer.class, new IntegerConverter());
        register(Byte.class, new ByteConverter());
        register(Float.class, new FloatConverter());
        register(Long.class, new LongConverter());
        register(Short.class, new ShortConverter());
        register(Double.class, new DoubleConverter());
        register(BigDecimal.class, new BigDecimalConverter());
        register(BigInteger.class, new BigIntegerConverter());
        register(Character.class, new CharacterConverter());
        register(Boolean.class, new BooleanConverter());
        
        register(int.class, new IntegerConverter());
        register(byte.class, new ByteConverter());
        register(float.class, new FloatConverter());
        register(long.class, new LongConverter());
        register(short.class, new ShortConverter());
        register(double.class, new DoubleConverter());
        register(char.class, new CharacterConverter());
        register(boolean.class, new BooleanConverter());
        
        register(String.class, new StringConverter());
        register(Date.class, new DateConverter());
        
        register(Array.class, new ArrayConverter());
        
        register(List.class, new ListConverter());
        register(Set.class, new SetConverter());
        register(Map.class, new MapConverter());
        register(Object.class, new ObjectConverter());
    }
    
    public void register(Class<?> type, ValueConverter<?> converter) {
        converters.put(type, converter);
    }
    
    public void unregister(Class<?> type) {
        converters.remove(type);
    }
    
    public Map<Type, ValueConverter<?>> getConverters() {
        return converters;
    }
    
    private static abstract class NumberConverter<T> implements ValueConverter<T> {
        @Override
        public T convert(Object value, Type type, ConvertParam param) {
            if (value instanceof String) {
                return convertString((String) value);
            }
            if (value instanceof Date) {
                return convertString(((Date) value).getTime() + "");
            }
            if (value instanceof Boolean) {
                return convertNumber(((Boolean) value).booleanValue() ? 1 : 0);
            }
            if (value instanceof Number) {
                return convertNumber((Number) value);
            }
            return null;
        }
        
        protected abstract T convertString(String s);
        
        protected abstract T convertNumber(Number n);
    }
    
    private static class IntegerConverter extends NumberConverter<Integer> {
        @Override
        protected Integer convertString(String s) {
            return Integer.valueOf(s);
        }
        
        @Override
        protected Integer convertNumber(Number n) {
            return Integer.valueOf(n.intValue());
        }
    }
    
    private static class FloatConverter extends NumberConverter<Float> {
        @Override
        protected Float convertString(String s) {
            return Float.valueOf(s);
        }
        
        @Override
        protected Float convertNumber(Number n) {
            return Float.valueOf(n.floatValue());
        }
    }
    
    private static class ShortConverter extends NumberConverter<Short> {
        @Override
        protected Short convertString(String s) {
            return Short.valueOf(s);
        }
        
        @Override
        protected Short convertNumber(Number n) {
            return Short.valueOf(n.shortValue());
        }
    }
    
    private static class ByteConverter extends NumberConverter<Byte> {
        @Override
        protected Byte convertString(String s) {
            return Byte.valueOf(s);
        }
        
        @Override
        protected Byte convertNumber(Number n) {
            return Byte.valueOf(n.byteValue());
        }
    }
    
    private static class LongConverter extends NumberConverter<Long> {
        @Override
        protected Long convertString(String s) {
            return Long.valueOf(s);
        }
        
        @Override
        protected Long convertNumber(Number n) {
            return Long.valueOf(n.longValue());
        }
    }
    
    private static class DoubleConverter extends NumberConverter<Double> {
        @Override
        protected Double convertString(String s) {
            return Double.valueOf(s);
        }
        
        @Override
        protected Double convertNumber(Number n) {
            return Double.valueOf(n.doubleValue());
        }
    }
    
    private static class BigDecimalConverter extends NumberConverter<BigDecimal> {
        @Override
        protected BigDecimal convertString(String s) {
            return new BigDecimal(s);
        }
        
        @Override
        protected BigDecimal convertNumber(Number n) {
            return new BigDecimal(n.toString());
        }
    }
    
    private static class BigIntegerConverter extends NumberConverter<BigInteger> {
        @Override
        protected BigInteger convertString(String s) {
            return new BigInteger(s);
        }
        
        @Override
        protected BigInteger convertNumber(Number n) {
            return new BigInteger(n.toString());
        }
    }
    
    private static class CharacterConverter implements ValueConverter<Character> {
        @Override
        public Character convert(Object value, Type type, ConvertParam param) {
            boolean booleanType = boolean.class.isAssignableFrom(value.getClass());
            if (value instanceof Boolean || booleanType) {
                boolean bool = false;
                if (value instanceof Boolean) {
                    bool = ((Boolean) value).booleanValue();
                }
                if (booleanType) {
                    bool = (Boolean) value;
                }
                int c = bool ? 1 : 0;
                return new Character((char) c);
            }
            return new Character(value.toString().charAt(0));
        }
    }
    
    private static class StringConverter implements ValueConverter<String> {
        @Override
        public String convert(Object value, Type type, ConvertParam param) {
            if (value instanceof Date) {
                return DateUtils.date2Str((Date) value);
            }
            if (value instanceof Double) {
                Double number = (Double) value;
                return BigDecimal.valueOf(number).toString();
            }
            if (value instanceof Long) {
                Long number = (Long) value;
                return BigDecimal.valueOf(number).toString();
            }
            return value.toString();
        }
    }
    
    private static class BooleanConverter implements ValueConverter<Boolean> {
        @Override
        public Boolean convert(Object value, Type type, ConvertParam param) {
            if ("1".equals(value.toString())) {
                return Boolean.TRUE;
            }
            if ("Y".equals(value.toString())) {
                return Boolean.TRUE;
            }
            return Boolean.valueOf(value.toString());
        }
    }
    
    private static class DateConverter implements ValueConverter<Date> {
        @Override
        public Date convert(Object value, Type type, ConvertParam param) throws Exception {
            if (value instanceof Number) {
                return new Date(((Number) value).longValue());
            }
            if (value instanceof String) {
                String dateStr = (String) value;
                if (dateStr.matches("\\d+")) {
                    return new Date(Long.parseLong(dateStr));
                }
                
                String pattern = param.getDateFormatPattern();
                if (pattern != null) {
                    return DateUtils.str2Date(dateStr, pattern);
                } else {
                    return DateFormat.tryParse(dateStr);
                }
            }
            return null;
        }
    }
    
    private static class ArrayConverter implements ValueConverter<Object> {
        
        @SuppressWarnings("unchecked")
        @Override
        public Object convert(Object value, Type type, ConvertParam param) throws Exception {
            if (value instanceof String) {
                String split = param.getSeparator();
                value = ((String) value).split(split);
            }
            Class<?> elementType = ((Class) type).getComponentType();
            
            if (value.getClass().isArray()) {
                if (!param.isDeepClone() && elementType == value.getClass().getComponentType()) {
                    return value;
                }
                
                int length = Array.getLength(value);
                Object result = Array.newInstance(elementType, length);
                
                int index = 0;
                for (; index < length; index++) {
                    Object e = Array.get(value, index);
                    Object v = ValueUtils.convert(e, elementType != null ? (Class<Object>) elementType : (Class<Object>) e.getClass());
                    Array.set(result, index, v);
                }
                
                return result;
            } else if (value instanceof Collection<?>) {
                Collection<?> coll = (Collection<?>) value;
                
                Object rslt = Array.newInstance(elementType, coll.size());
                
                int index = 0;
                for (Object e : coll) {
                    Object v = ValueUtils.convert(e, elementType != null ? (Class<Object>) elementType : (Class<Object>) e.getClass());
                    Array.set(rslt, index++, v);
                }
                
                return rslt;
            }
            
            return null;
        }
        
    }
    
    @SuppressWarnings("rawtypes")
    private static abstract class CollectionConverter<T extends Collection> implements ValueConverter<T> {
        @SuppressWarnings("unchecked")
        @Override
        public T convert(Object value, Type type, ConvertParam param) throws Exception {
            T rslt = getCollection();
            
            if (value instanceof String) {
                String split = param.getSeparator();
                value = ((String) value).split(split);
            }
            Type elementType = param.getTargetType();
            if (elementType == null) {
                elementType = ReflectUtils.getArgumentType(type, 0);
            }
            
            if (value.getClass().isArray()) {
                int length = Array.getLength(value);
                for (int i = 0; i < length; i++) {
                    Object e = Array.get(value, i);
                    Object v = ValueUtils.convert(e, elementType != null ? (Class<T>) elementType : (Class<T>) e.getClass());
                    rslt.add(v);
                }
            } else if (value instanceof Collection<?>) {
                Collection<?> coll = (Collection<?>) value;
                if (coll.isEmpty()) {
                    return (T) coll;
                }
                
                if (!param.isDeepClone() && elementType == ReflectUtils.getCollectionArgumentType(coll)) {
                    return (T) value;
                }
                
                for (Object e : coll) {
                    Object v = ValueUtils.convert(e, elementType != null ? (Class<T>) elementType : (Class<T>) e.getClass());
                    rslt.add(v);
                }
            }
            
            return rslt;
        }
        
        protected abstract T getCollection();
        
    }
    
    private static class MapConverter implements ValueConverter<Map<?, ?>> {
        @Override
        public Map<?, ?> convert(Object value, Type type, ConvertParam param) throws Exception {
            if (value instanceof Map) {
                Map mapValue = (Map) value;
                Type sourceType = ReflectUtils.getArgumentType(mapValue.getClass(), 1);
                Type targetType = ReflectUtils.getArgumentType(type, 1);
                if (sourceType != null && targetType != null && sourceType == targetType) {
                    //值类型一致时直接返回
                    return Maps.newLinkedHashMap(mapValue);
                }
                Map result = Maps.newLinkedHashMapWithExpectedSize(mapValue.size());
                param.getBeanMapperIfAbsent(BeanMapper::create).copyMapToMap(mapValue, result, (Class) targetType);
                return result;
            }
    
            Map result = Maps.newLinkedHashMap();
            param.getBeanMapperIfAbsent(BeanMapper::create).copyBeanToMap(value, result);
            return result;
        }
    }
    
    private static class ObjectConverter<T> implements ValueConverter<T> {
        @Override
        public T convert(Object value, Type type, ConvertParam param) throws Exception {
            if (value instanceof Map) {
                return (T) param.getBeanMapperIfAbsent(BeanMapper::create).copyMapToBean((Map) value, (Class) type);
            }
            if (!param.isDeepClone() && ReflectUtils.isSuper(type, value.getClass())) {
                return (T) value;
            }
            return (T) param.getBeanMapperIfAbsent(BeanMapper::create).copy(value, (Class) type);
        }
    }
    
    private static class ListConverter extends CollectionConverter<List<?>> {
        @Override
        protected List<?> getCollection() {
            return new ArrayList<>();
        }
    }
    
    private static class SetConverter extends CollectionConverter<Set<?>> {
        @Override
        protected Set<?> getCollection() {
            return new HashSet<>();
        }
    }
    
    
}