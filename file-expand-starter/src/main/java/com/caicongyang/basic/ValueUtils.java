package com.caicongyang.basic;


import com.caicongyang.utils.ReflectUtils;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.springframework.beans.BeanUtils;

/**
 * @author WuBo
 * @CreateDate 2012-8-8 下午02:32:38
 */
@SuppressWarnings("unchecked")
public class ValueUtils {

    public static <T> T ifNull(T value, T defaultValue) {
        return value != null ? value : defaultValue;
    }

    public static String ifBlank(String value, String defaultValue) {
        return hasText(value) ? value : defaultValue;
    }

    @Deprecated
    public static <T> T ifTrue(T value, T expectedValue, T elseValue) {
        return ifEquals(value, expectedValue, elseValue);
    }

    public static <T> T ifEquals(T value, T expectedValue, T elseValue) {
        if (value == expectedValue || expectedValue.equals(value)) {
            return value;
        }
        return elseValue;
    }

    public static <T> T ifNot(T value, T unexpectedValue, T elseValue) {
        if (value != unexpectedValue || !unexpectedValue.equals(value)) {
            return value;
        }
        return elseValue;
    }

    public static boolean hasLength(CharSequence str) {
        return (str != null && str.length() > 0);
    }

    public static boolean hasText(CharSequence str) {
        if (!hasLength(str)) {
            return false;
        }

        int strLen = str.length();
        for (int i = 0; i < strLen; i++) {
            if (!Character.isWhitespace(str.charAt(i))) {
                return true;
            }
        }
        return false;
    }

    public static String safe2Str(Object obj) {
        return safe2Str(obj, "");
    }

    public static String safe2Str(Object obj, String nullValue) {
        if (obj != null) {
            return obj.toString();
        }
        return nullValue;
    }

    public static <T> T[] convertArray(Object[] values, Class<T> type) {
        if (values == null) {
            return null;
        }
        Object[] rslt = (Object[]) Array.newInstance(type, values.length);
        int idx = 0;
        for (Object val : values) {
            rslt[idx++] = convert(val, type);
        }
        return (T[]) rslt;
    }

    public static <T> T convert(Object value, Class<T> type) {
        return convert(value, type, ConvertParam.EMPTY);
    }

    public static <T> T convert(Object value, ParameterizedType type) {
        return convert(value, type, ConvertParam.EMPTY);
    }

    public static <T> T convert(Object value, Class<T> type, ConvertParam param) {
        return doConvert(value, type, param);
    }

    public static <T> T convert(Object value, ParameterizedType type, ConvertParam param) {
        return doConvert(value, type, param);
    }

    private static <T> T doConvert(Object value, Type type, ConvertParam param) {
        if (value == null) {
            return null;
        }
        if (value instanceof String) {
            if (type instanceof Class && String.class.isAssignableFrom((Class) type)) {
                return (T) value;
            }
            if (!hasText((String) value)) {
                return null;
            }
        }
        if (type == null) {
            return (T) value;
        }
        try {
            if ((type instanceof Class) && ((Class) type).isInstance(value)) {
                if (BeanUtils.isSimpleProperty((Class) type) || !param.isDeepClone()) {
                    return (T) value;
                }
            }

            Map<Type, ValueConverter<?>> converterMap = ValueConverterRegistry.getInstance()
                .getConverters();
            ValueConverter<T> converter = (ValueConverter<T>) converterMap.get(type);
            if (converter == null) {
                if ((type instanceof Class) && ((Class) type).isArray()) {
                    converter = (ValueConverter<T>) converterMap.get(Array.class);
                } else if (ReflectUtils.isSub(type, List.class)) {
                    converter = (ValueConverter<T>) converterMap.get(List.class);
                } else if (ReflectUtils.isSub(type, Set.class)) {
                    converter = (ValueConverter<T>) converterMap.get(Set.class);
                } else if (ReflectUtils.isSub(type, Map.class)) {
                    converter = (ValueConverter<T>) converterMap.get(Map.class);
                } else if ((type instanceof Class) && !BeanUtils.isSimpleValueType((Class) type)) {
                    converter = (ValueConverter<T>) converterMap.get(Object.class);
                } else {
                    throw new RuntimeException(
                        "The type " + type + " has not supported yet.");
                }
            }
            T v = (T) converter.convert(value, type, param);
            if (v != null) {
                return v;
            }
            throw new ClassCastException(
                value.getClass().getName() + " cannot be cast to " + type.getTypeName()
                    + ", value: " + value);
        } catch (Exception e) {
            ClassCastException ex = new ClassCastException(
                value.getClass().getName() + " cannot be cast to "
                    + type.getTypeName() + ", value: " + value + ", reason: " + e.getMessage());
            throw ex;
        }
    }

    /**
     * <h3>给定值，返回期望的类型</h3>
     *
     * @deprecated
     */
    @Deprecated
    public static <T> T convert(Object value, Class<T> type, Object[] extraParams) {
        return doConvert(value, type, new ConvertParam(extraParams));
    }

    /**
     * <h3>给定值，返回期望的类型</h3>
     *
     * @param type 带泛型的类型
     * @deprecated
     */
    @Deprecated
    public static <T> T convert(Object value, ParameterizedType type, Object[] extraParams) {
        return doConvert(value, type, new ConvertParam(extraParams));
    }

    public static boolean safeEquals(Object o1, Object o2, boolean onBothNull) {
        if (o1 == o2) {
            return true;
        }
        if (o1 == null && o2 == null) {
            return onBothNull;
        }
        if (o1 != null) {
            return o1.equals(o2);
        }
        return o2.equals(o1);
    }

    public static void setScaleForBean(Object bean, int scale, RoundingMode mode)
        throws InvocationTargetException, IllegalAccessException {

        PropertyDescriptor[] pds = ReflectUtils.getPropertyDescriptors(bean.getClass());
        for (PropertyDescriptor pd : pds) {
            if (pd.getPropertyType() != null && BigDecimal.class
                .isAssignableFrom(pd.getPropertyType())) {
                Method read = pd.getReadMethod();
                Method write = pd.getWriteMethod();
                if (read != null && write != null) {
                    BigDecimal value = (BigDecimal) read.invoke(bean);
                    if (value != null && value.scale() != scale) {
                        value = value.setScale(scale, mode);
                        write.invoke(bean, value);
                    }
                }
            }
        }

    }

}
