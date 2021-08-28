package com.caicongyang.basic;

import com.caicongyang.excel.export.DataExporter;
import com.caicongyang.utils.ReflectUtils;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cglib.beans.BeanCopier;
import org.springframework.cglib.beans.BeanMap;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/**
 * Bean属性映射
 *
 * @author wubo
 * @date 2020-08-18
 */
public class BeanMapper {


    private static Logger logger = LoggerFactory.getLogger(DataExporter.class);


    private final static Map<String, BeanCopier> COPIER_MAP = new HashMap<>();

    private Class<? extends Annotation> includeAnnotation;
    private Class<? extends Annotation> excludeAnnotation;
    private List<String> includeProperties;
    private List<String> excludeProperties;
    private boolean containsStaticFields;
    private boolean skipNullValue;
    private boolean autoType = true;
    private boolean deepClone;
    private ConvertParam param = new ConvertParam().beanMapper(this);

    private Map<String, String> keyMapping;
    private Map<Object, Function<Object, Object>> valueMapping;

    public static BeanMapper create() {
        return new BeanMapper();
    }

    public BeanMapper includeAnnotation(Class<? extends Annotation> annotation) {
        this.includeAnnotation = annotation;
        return this;
    }

    public BeanMapper excludeAnnotation(Class<? extends Annotation> annotation) {
        this.excludeAnnotation = annotation;
        return this;
    }

    public BeanMapper includeProperties(String... properties) {
        if (properties != null) {
            if (properties.length > 0) {
                this.includeProperties = Arrays.asList(properties);
            }
        }
        return this;
    }

    public BeanMapper excludeProperties(String... properties) {
        if (properties != null) {
            if (properties.length > 0) {
                this.excludeProperties = Arrays.asList(properties);
            }
        }
        return this;
    }

    public BeanMapper containsStaticFields() {
        this.containsStaticFields = true;
        return this;
    }

    public BeanMapper skipNullValue() {
        this.skipNullValue = true;
        return this;
    }

    public BeanMapper disableSkipNullValue() {
        this.skipNullValue = false;
        return this;
    }

    public BeanMapper deepClone() {
        this.deepClone = true;
        this.param.deepCopy(true);
        return this;
    }

    public BeanMapper disableDeepClone() {
        this.deepClone = false;
        this.param.deepCopy(false);
        return this;
    }

    public BeanMapper autoType() {
        this.autoType = true;
        return this;
    }

    public BeanMapper disableAutoType() {
        this.autoType = false;
        return this;
    }

    public BeanMapper keyMapping(String sourceProperty, String targetProperty) {
        if (keyMapping == null) {
            keyMapping = new HashMap<>();
        }
        keyMapping.put(sourceProperty, targetProperty);
        return this;
    }

    public <K> BeanMapper valueMapping(K sourceProperty, Function<Object, Object> func) {
        if (valueMapping == null) {
            valueMapping = new HashMap<>();
        }
        valueMapping.put(sourceProperty, func);
        return this;
    }

    /**
     * 从Bean:source复制到自动实例化的Bean
     */
    public <T> T copy(Object source, Class<T> targetClass)
        throws IllegalAccessException, InstantiationException, InvocationTargetException {
        T target = targetClass.newInstance();
        copy(source, target);
        return target;

    }

    /**
     * 从Bean:source复制到Bean:target
     */
    public void copy(Object source, Object target)
        throws InvocationTargetException, IllegalAccessException {
        Assert.notNull(source, "Parameter source is required");
        Assert.notNull(target, "Parameter target is required");

        Class<?> targetClass = target.getClass();
        Map<Method, Object> targetOriginValues = null;
        if (this.isCopyAllProperties()) {
            PropertyDescriptor[] pds = this.getExcludePropertyDescriptors(targetClass);
            if (pds != null) {
                targetOriginValues = Maps.newHashMap();
                for (PropertyDescriptor pd : pds) {
                    Method readMethod = pd.getReadMethod();
                    Method writeMethod = pd.getWriteMethod();
                    if (readMethod != null && writeMethod != null) {
                        Object value = readMethod.invoke(target);
                        targetOriginValues.put(writeMethod, value);
                    }
                }
            }
            boolean useConverter = autoType || deepClone;
            String copierKey =
                source.getClass().getName() + "|" + targetClass.getName() + "|" + useConverter;
            BeanCopier copier = COPIER_MAP.get(copierKey);
            if (copier == null) {
                copier = BeanCopier.create(source.getClass(), targetClass, useConverter);
                COPIER_MAP.put(copierKey, copier);
            }
            copier.copy(source, target, useConverter ? (value, target1, setter) -> {
                if (setter != null && setter.toString().startsWith("set")) {
                    String field = StringUtils.uncapitalize(setter.toString().substring(3));
                    if (Collection.class.isAssignableFrom(target1)) {
                        Method targetSetter = ReflectUtils.setter4Safe(targetClass, field);
                        return this.getTargetValueAuto(field, value,
                            targetSetter.getGenericParameterTypes()[0]);
                    }
                    return this.getTargetValue(field, value, target1);
                }
                return doConvertValue(value, target1);
            } : null);

            if (pds != null && targetOriginValues != null) {
                for (Map.Entry<Method, Object> entry : targetOriginValues.entrySet()) {
                    Method writeMethod = entry.getKey();
                    writeMethod.invoke(target, entry.getValue());
                }
            }
        } else {
            PropertyDescriptor[] pds = this.getPropertyDescriptors(source.getClass());
            for (PropertyDescriptor pd : pds) {
                Method readMethod = pd.getReadMethod();
                Object value = readMethod.invoke(source);
                if (value == null && skipNullValue) {
                    continue;
                }

                PropertyDescriptor tgtPd = ReflectUtils
                    .getPropertyDescriptor(targetClass, this.getTargetProperty(pd.getName()));
                if (tgtPd == null) {
                    continue;
                }
                Method writeMethod = tgtPd.getWriteMethod();

                if (writeMethod != null && Modifier.isPublic(writeMethod.getModifiers())
                    && !Modifier.isStatic(writeMethod.getModifiers())) {
                    writeMethod.invoke(target, !autoType ? this
                        .getTargetValue(pd.getName(), value, writeMethod.getParameterTypes()[0])
                        : this.getTargetValueAuto(pd.getName(), value,
                            writeMethod.getGenericParameterTypes()[0]));
                }
            }
        }

    }

    /**
     * 复制: source和target可以是Bean或者Map，自动识别
     */
    public void copyAuto(Object source, Object target)
        throws InvocationTargetException, IllegalAccessException {
        Assert.notNull(source, "Parameter source is required");
        Assert.notNull(target, "Parameter target is required");

        Class srcClass = source.getClass();
        Class targetClass = target.getClass();

        boolean isSrcMap = Map.class.isAssignableFrom(srcClass);
        boolean isTargetMap = Map.class.isAssignableFrom(targetClass);

        if (!isSrcMap) {
            if (isTargetMap) {
                copyBeanToMap(source, (Map<String, Object>) target);
            } else {
                copy(source, target);
            }
        } else {
            if (isTargetMap) {
                copyMapToMap((Map<String, Object>) source, (Map<String, Object>) target);
            } else {
                copyMapToBean((Map<String, Object>) source, target);
            }
        }

    }

    /**
     * 列表复制：从Bean:source列表复制到自动实例化的Bean列表
     */
    public <T> List<T> copyList(List<? extends Object> sources, Class<T> targetClass)
        throws IllegalAccessException, InvocationTargetException, InstantiationException {
        Assert.notNull(sources, "Parameter sources is required");

        List<T> result = new ArrayList<T>();
        for (Object obj : sources) {
            result.add(copy(obj, targetClass));
        }
        return result;
    }


    /**
     * 列表复制：从Bean:source列表复制到自动实例化的Bean列表
     */
    public <T> List<T> copyListAuto(List<? extends Object> sources, Class<T> targetClass)
        throws IllegalAccessException, InstantiationException, InvocationTargetException {
        Assert.notNull(sources, "Parameter sources is required");

        List<T> result = new ArrayList<T>();
        for (Object obj : sources) {
            T target = targetClass.newInstance();
            copyAuto(obj, target);
            result.add(target);
        }
        return result;

    }

    /**
     * 复制列表: List Map复制到List Bean
     */
    public <T> List<T> copyMapsToBeans(List<Map<String, Object>> sources, Class<T> beanClass)
        throws InstantiationException, IllegalAccessException {
        if (sources == null) {
            return null;
        }
        if (sources.size() == 0) {
            return Collections.EMPTY_LIST;
        }

        List<T> target = Lists.newArrayListWithExpectedSize(sources.size());
        for (Map<String, Object> source : sources) {
            target.add(copyMapToBean(source, beanClass));
        }
        return target;
    }

    /**
     * 复制列表: List Bean复制到List Map
     */
    public List<Map<String, Object>> copyBeansToMaps(List<? extends Object> sources)
        throws InvocationTargetException, IllegalAccessException {
        if (sources == null) {
            return null;
        }
        if (sources.size() == 0) {
            return Collections.EMPTY_LIST;
        }

        List<Map<String, Object>> maps = Lists.newArrayListWithExpectedSize(sources.size());

        PropertyDescriptor[] pds = this.getPropertyDescriptors(sources.get(0).getClass());

        for (Object bean : sources) {
            Map<String, Object> map = Maps.newHashMapWithExpectedSize(pds.length);

            for (PropertyDescriptor pd : this.getPropertyDescriptors(bean.getClass())) {
                Object value = pd.getReadMethod().invoke(bean);
                if (value == null && skipNullValue) {
                    continue;
                }
                map.put(this.getTargetProperty(pd.getName()),
                    this.getTargetValue(pd.getName(), value, null));
            }
            maps.add(map);
        }
        return maps;

    }

    /**
     * 从Map:source复制到Map:target
     */
    public <K> void copyMapToMap(Map<K, Object> source, Map<K, Object> target) {
        copyMapToMap(source, target, null);
    }

    /**
     * 从Map:source复制到Map:target，可指定Map的值类型
     */
    public <K> void copyMapToMap(Map<K, Object> source, Map<K, Object> target,
        Class<?> targetValueType) {
        this.onEntrySet(source, (k, v) -> {
            if (v != null || !skipNullValue) {
                target.put(k, this.getTargetValue(k, v, targetValueType));
            }
        });
    }


    /**
     * 从Bean:source复制到自动创建的HashMap:target
     */
    public Map<String, Object> copyBeanToMap(Object source) {
        if (source == null) {
            return null;
        }
        Map<String, Object> map = Maps.newHashMap();
        copyBeanToMap(source, map);
        return map;
    }

    /**
     * 从Bean:source复制到Map:target
     */
    public void copyBeanToMap(Object source, Map<String, Object> target) {
        if (source == null || target == null) {
            return;
        }
        BeanMap map = BeanMap.create(source);
        copyMapToMap(map, target);
    }

    /**
     * 从Map:source复制到自动实例化的Bean
     */
    public <T> T copyMapToBean(Map<String, Object> source, Class<T> beanClass)
        throws IllegalAccessException, InstantiationException {

        T target = beanClass.newInstance();

        copyMapToBean(source, target);

        return target;

    }

    /**
     * 从Map:source复制到Bean:target
     */
    public void copyMapToBean(Map<String, Object> source, Object target) {
        this.onEntrySet(source, (k, v) -> {
            try {
                PropertyDescriptor pd = ReflectUtils.getPropertyDescriptor(target.getClass(), k);
                if (pd != null) {
                    if (v != null || !skipNullValue) {
                        Method setter = pd.getWriteMethod();
                        if (setter != null
                            && Modifier.isPublic(setter.getModifiers())
                            && (!Modifier.isStatic(setter.getModifiers()) || this
                            .isContainsStaticFields())) {
                            setter.invoke(target, !autoType ? this
                                .getTargetValue(pd.getName(), v, setter.getParameterTypes()[0])
                                : this.getTargetValueAuto(pd.getName(), v,
                                    setter.getGenericParameterTypes()[0]));
                        }
                    }
                }
            } catch (Exception e) {
                logger.error("类型转换异常", e);
            }
        });
    }

    private PropertyDescriptor[] getExcludePropertyDescriptors(Class<?> beanClass) {
        if (excludeAnnotation == null && excludeProperties == null) {
            return null;
        }
        List<PropertyDescriptor> result = Lists.newArrayList();

        PropertyDescriptor[] pds = ReflectUtils.getPropertyDescriptors(beanClass);
        for (PropertyDescriptor pd : pds) {
            if (excludeProperties != null && excludeProperties.contains(pd.getName())) {
                result.add(pd);
                continue;
            }
            Method getter = pd.getReadMethod();
            if (getter != null
                && Modifier.isPublic(getter.getModifiers())
                && (!Modifier.isStatic(getter.getModifiers()) || containsStaticFields)) {
                if (excludeAnnotation != null) {
                    if (getter.isAnnotationPresent(excludeAnnotation)) {
                        result.add(pd);
                        continue;
                    }
                }
            }
        }
        if (result.isEmpty()) {
            return null;
        }
        return result.toArray(new PropertyDescriptor[result.size()]);
    }


    private PropertyDescriptor[] getPropertyDescriptors(Class<?> beanClass) {
        List<PropertyDescriptor> result = Lists.newArrayList();

        PropertyDescriptor[] pds = ReflectUtils.getPropertyDescriptors(beanClass);
        boolean includeMode = includeProperties != null || includeAnnotation != null;

        for (PropertyDescriptor pd : pds) {
            if (excludeProperties != null && excludeProperties.contains(pd.getName())) {
                continue;
            }
            Method getter = pd.getReadMethod();
            if (getter != null
                && Modifier.isPublic(getter.getModifiers())
                && (!Modifier.isStatic(getter.getModifiers()) || containsStaticFields)) {
                if (excludeAnnotation != null) {
                    if (getter.isAnnotationPresent(excludeAnnotation)) {
                        continue;
                    }
                }
                if (includeProperties != null && includeProperties.contains(pd.getName())) {
                    result.add(pd);
                }
                if (includeAnnotation != null) {
                    if (getter.isAnnotationPresent(includeAnnotation)) {
                        result.add(pd);
                    }
                }
                if (!includeMode) {
                    result.add(pd);
                }
            }
        }

        return result.toArray(new PropertyDescriptor[result.size()]);
    }

    private <K> void onEntrySet(Map<K, Object> data, BiConsumer<K, Object> consumer) {
        if (data != null) {
            for (Map.Entry<K, Object> entry : data.entrySet()) {
                if (excludeProperties != null && excludeProperties.contains(entry.getKey())) {
                    continue;
                }
                K key = entry.getKey();
                if (key != null && key instanceof String) {
                    key = (K) getTargetProperty((String) key);
                }
                if (includeProperties != null) {
                    if (includeProperties.contains(entry.getKey())) {
                        consumer.accept(key, entry.getValue());
                    }
                } else {
                    consumer.accept(key, entry.getValue());
                }
            }
        }
    }

    private String getTargetProperty(String sourceProperty) {
        if (keyMapping == null) {
            return sourceProperty;
        }
        String targetProperty = keyMapping.get(sourceProperty);
        if (targetProperty == null) {
            return sourceProperty;
        }
        return targetProperty;
    }

    private <K> Object getTargetValueAuto(K sourceProperty, Object sourceValue, Type targetType) {
        if (targetType instanceof ParameterizedType) {
            return getTargetValueWithParameterizedType(sourceProperty, sourceValue,
                (ParameterizedType) targetType);
        }
        return getTargetValue(sourceProperty, sourceValue, (Class<?>) targetType);
    }

    private <K> Object getTargetValue(K sourceProperty, Object sourceValue, Class<?> targetType) {
        if (valueMapping == null) {
            return targetType != null ? doConvertValue(sourceValue, targetType) : sourceValue;
        }
        Function<Object, Object> func = valueMapping.get(sourceProperty);
        if (func == null) {
            return targetType != null ? doConvertValue(sourceValue, targetType) : sourceValue;
        }
        return func.apply(sourceValue);
    }

    private <K> Object getTargetValueWithParameterizedType(K sourceProperty, Object sourceValue,
        ParameterizedType targetType) {
        if (valueMapping == null) {
            return targetType != null ? doConvertValueWithParameterizedType(sourceValue, targetType)
                : sourceValue;
        }
        Function<Object, Object> func = valueMapping.get(sourceProperty);
        if (func == null) {
            return targetType != null ? doConvertValueWithParameterizedType(sourceValue, targetType)
                : sourceValue;
        }
        return func.apply(sourceValue);
    }

    private <K> Object doConvertValue(K sourceValue, Class<?> targetType) {
        return deepClone || autoType ? ValueUtils.convert(sourceValue, targetType, param)
            : sourceValue;
    }

    private <K> Object doConvertValueWithParameterizedType(K sourceValue,
        ParameterizedType targetType) {
        return deepClone || autoType ? ValueUtils.convert(sourceValue, targetType, param)
            : sourceValue;
    }

    private boolean isContainsStaticFields() {
        return containsStaticFields;
    }

    private boolean isCopyAllProperties() {
        return includeAnnotation == null && includeProperties == null
            && keyMapping == null;
    }

}
