package com.caicongyang;


import com.caicongyang.basic.ConvertParam;

public class ColDataType {
    public final static ColDataType STRING_TYPE = ColDataType.of(String.class);
    
    public static ColDataType of(Class<?> type) {
        return of(type, null);
    }
    
    public static ColDataType of(Class<?> type, ConvertParam convertParam) {
        ColDataType inst = new ColDataType();
        inst.type = type;
        inst.convertParam = convertParam;
        return inst;
    }
    
    private Class<?> type;
    private ConvertParam convertParam;
    
    public Class<?> getType() {
        return type;
    }
    public String getDateFormat() {
        return convertParam != null ? convertParam.getDateFormatPattern() : null;
    }
    public String getSeparator() {
        return convertParam != null ? convertParam.getSeparator() : null;
    }
    
}
