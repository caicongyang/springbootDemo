package com.caicongyang.basic;

import com.caicongyang.basic.ConvertParam;
import java.lang.reflect.Type;

/**
 * @author bo.wu
 * @CreateDate 2014年7月23日
 */
public interface ValueConverter<V> {

	V convert(Object value, Type type, ConvertParam param) throws Exception;
	
}