package com.caicongyang.basic;

import java.lang.reflect.Type;
import java.util.function.Supplier;

public class ConvertParam {

    final static ConvertParam EMPTY = new ConvertParam();

    private String dateFormatPattern;
    private String separator;
    private Type targetType;
    private boolean deepClone;
    private BeanMapper beanMapper;

    private Object[] params;

    public ConvertParam() {
    }

    public ConvertParam(Object[] params) {
        this.params = params;
    }

    public ConvertParam dateFormatPattern(String dateFormatPattern) {
        this.dateFormatPattern = dateFormatPattern;
        return this;
    }

    public ConvertParam arraySeparator(String separator) {
        this.separator = separator;
        return this;
    }

    public ConvertParam targetType(Type targetType) {
        this.targetType = targetType;
        return this;
    }

    public ConvertParam deepCopy(boolean deepCopy) {
        this.deepClone = deepCopy;
        return this;
    }

    public ConvertParam beanMapper(BeanMapper beanMapper) {
        this.beanMapper = beanMapper;
        return this;
    }

    public String getDateFormatPattern() {
        if (dateFormatPattern == null && params != null && params.length > 0) {
            return params[0] instanceof String ? (String) params[0] : null;
        }
        return dateFormatPattern;
    }

    public String getSeparator() {
        if (separator == null) {
            separator = ",";
            if (params != null && params.length > 0) {
                separator = params[0] instanceof String ? (String) params[0] : separator;
            }
        }
        return separator;
    }

    public Type getTargetType() {
        if (targetType == null && params != null && params.length > 0) {
            if (params[0] instanceof Type) {
                return (Type) params[0];
            } else if (params.length > 1) {
                return params[1] instanceof Type ? (Type) params[1] : null;
            }
        }
        return targetType;
    }

    public boolean isDeepClone() {
        return deepClone;
    }

    public BeanMapper getBeanMapper() {
        return beanMapper;
    }

    public BeanMapper getBeanMapperIfAbsent(Supplier<BeanMapper> ifAbsent) {
        if (beanMapper == null) {
            beanMapper = ifAbsent.get();
        }
        return beanMapper;
    }
}
