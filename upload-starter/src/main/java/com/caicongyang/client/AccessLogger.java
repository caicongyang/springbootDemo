

package com.caicongyang.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * 
 * 
 * @author pengrongxin
 * @version 1.0  2017-02-28 下午04:06:42
 * @since 1.0
 */
public class AccessLogger {

	private static final Logger access = LoggerFactory.getLogger("access");
	
	public static void log(Object message) {
		access.info(String.valueOf(message));
	}
	
	public static void log(Object message, Throwable t) {
		access.info(String.valueOf(message), t);
	}
}
