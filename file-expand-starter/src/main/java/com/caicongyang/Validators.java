package com.caicongyang;

import org.apache.commons.lang3.StringUtils;

public class Validators {
	
	public static void notNull(Object value, String name) {
		if (value == null) throw new DataValidateException("notNull", name);
	}
	
	public static void notBlank(String value, String name) {
	    if (StringUtils.isEmpty(name)) throw new DataValidateException("notBlank", name);
	}
	
	public static void maxLength(String name, long maxLength, long actualLength) {
		if (actualLength > maxLength) throw new DataValidateException("maxLength", name, maxLength, actualLength);
	}
    
    public static void minLength(String name, long minLength, long actualLength) {
        if (actualLength < minLength) throw new DataValidateException("minLength", name, minLength, actualLength);
    }
    
}
