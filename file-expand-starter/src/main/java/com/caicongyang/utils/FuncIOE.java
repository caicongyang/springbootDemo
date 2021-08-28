package com.caicongyang.utils;

/**
 * 单参数函数
 * 
 * @param <I> 参数类型
 * @param <O> 返回类型
 * @param <E> 抛出异常的类型
 * @author ZhangXiaoye
 * @date 2016年6月20日 下午4:50:36
 */
public interface FuncIOE<I, O, E extends Exception> {
	
	public O call(final I input) throws E;

}
