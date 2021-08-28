package com.caicongyang;

/**
 * @author WuBo
 * @CreateDate 2014-3-13 下午4:49:54
 */
public class ExceptionUtils {
	
	public static RuntimeException wrap2Runtime(Exception e) {
		if (e instanceof RuntimeException) {
			return (RuntimeException) e;
		}
		return new RuntimeException(e);
	}
	/**
	 * 得到包装类型异常的原始异常
	 * @author WuBo
	 * @CreateDate 2011-11-28 下午02:07:39
	 * @param e
	 * @return
	 */
	public static Throwable getOriginal(Throwable e){
		Throwable ex = e.getCause();
		if(ex != null){
			return getOriginal(ex);
		}else{
			return e;
		}
	}
}
