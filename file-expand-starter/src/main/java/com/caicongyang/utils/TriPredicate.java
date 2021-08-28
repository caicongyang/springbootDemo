package com.caicongyang.utils;
/**
 * 三个入参的TriPredicate
 * @author WuBo
 * @date 2019年11月20日
 * @param <F> first
 * @param <S> second
 * @param <T> third
 */
@FunctionalInterface
public interface TriPredicate<F, S, T> {
    
    boolean test(F f, S s, T t);
}
