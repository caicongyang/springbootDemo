package com.caicongyang;

import java.util.ResourceBundle;

public class DataValidateMessages {
	private static ResourceBundle rb;
	private final static String LANG_BASE_NAME = "validator/validate_message";
	static {
		try {
			rb = ResourceBundle.getBundle(LANG_BASE_NAME); 
		} catch (Exception e) {
			System.err.println(e.getMessage());
		}
	}
	
	public static String get(String key, Object... params) {
        String value = key;
		try {
			if (rb != null) value = rb.getString(key);
		} catch (Exception e) {
		   // ignore
		}
        if (params != null) {
            for (int i=0; i<params.length; i++) {
                value = value.replaceAll("\\{"+i+"\\}", params[i].toString());
            }
        }
        return value;
	}
	
}
