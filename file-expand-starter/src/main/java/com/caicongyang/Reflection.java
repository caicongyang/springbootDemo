package com.caicongyang;

import com.caicongyang.utils.FuncIOE;
import com.esotericsoftware.reflectasm.MethodAccess;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Reflection {
	
	private static final Logger logger = LoggerFactory.getLogger(Reflection.class);
	
	/**
	 * 对大批量查询拆分成多个小批次调用的工具方法，所有批次执行完成后汇总返回结果。<br/>
	 * 如果一个小批次返回的List为空，则中途停止，否则直至全部批次完成后汇总。<br/>
	 * 调用查询接口时的异常会被抛出。<br/>
	 * 使用方式请见{@link ReflectionTest#test()}
	 * 
	 * @param client 需要调用的客户端
	 * @param methodName 客户端方法名
	 * @param inputDTO 待拆分的批量调用入参
	 * @param batchSize 每批次大小
	 * @param inputListGetter 从入参获取批量数据的List<b>引用</b>，必须是直接返回引用。
	 * @param outputListGetter 从出参获取批量数据的List<b>引用</b>，必须是直接返回引用。
	 * @return 汇总好的的批量调用出参
	 * @throws IllegalArgumentException 当methodName不存在、反射调用出错的情况；或者batchSize小于等于0时。
	 * @throws NullPointerException 当传入参数为null时
	 * @author ZhangXiaoye
	 * @date 2016年9月30日 下午5:52:30
	 */
	@SuppressWarnings("unchecked")
	public static <InputDTO, OutputDTO, I, O, T extends Exception> OutputDTO batchInvoke(Object client, String methodName, InputDTO inputDTO, int batchSize, FuncIOE<InputDTO, List<I>, T> inputListGetter, FuncIOE<OutputDTO, List<O>, T> outputListGetter) throws T{
		final Method method;
		try{
			method = client.getClass().getDeclaredMethod(methodName, inputDTO.getClass());
			if(! Modifier.isPublic(method.getModifiers())){
				method.setAccessible(true);
			}
		}catch(NoSuchMethodException e){
			throw new IllegalArgumentException(e);
		}
		if(batchSize <= 0){
			throw new IllegalArgumentException("batchInvoke argument: batchSize must > 0, but get " + batchSize);
		}
		final List<I> inputList = inputListGetter.call(inputDTO);
		if(inputList == null || inputList.size() <= batchSize){
			try {
				return (OutputDTO) method.invoke(client, inputDTO);
			} catch (InvocationTargetException e) {
				throw new IllegalArgumentException(e);
			} catch (IllegalAccessException e) {
				throw new IllegalArgumentException(e);
			}
		}
		final List<I> originalInputList = new ArrayList<I>(inputList);
		OutputDTO batchOutputDTO = null;
		try{
			final List<List<I>> batches = split(inputList, batchSize);
			final List<O> outputs = new ArrayList<O>(batchSize * 2);
			
			List<O> batchOutputList = null;
			for(final List<I> batch: batches){
				inputList.clear();
				inputList.addAll(batch);
				try {
					batchOutputDTO = (OutputDTO) method.invoke(client, inputDTO);
					if(batchOutputDTO != null){
						batchOutputList = outputListGetter.call(batchOutputDTO);
					}else{
						batchOutputList = null;
					}
				} catch (InvocationTargetException e) {
					throw new IllegalArgumentException(e);
				} catch (IllegalAccessException e) {
					throw new IllegalArgumentException(e);
				}
				if(batchOutputList == null || batchOutputList.isEmpty()){
					break;
				}
				outputs.addAll(batchOutputList);
			}
			if(batchOutputDTO != null && batchOutputList != null){
				batchOutputList.clear();
				batchOutputList.addAll(outputs);
			}
		}catch(RuntimeException e){
			throw e;
		}finally{
			// 确保任何情况下，不改变原始参数
			inputList.clear();
			inputList.addAll(originalInputList);
		}
		return batchOutputDTO;
	}
	
	/**
	 * 按照批次大小拆分列表，供{@link #batchInvoke(Object, String, Object, int, FuncIOE, FuncIOE)}调用
	 */
	private static <E> List<List<E>> split(List<E> list, int batchSize){
		List<List<E>> ret = new ArrayList<List<E>>();
		for(int i = 0; i < list.size(); i += batchSize){
			ret.add(new ArrayList<E>(list.subList(i, Math.min(i + batchSize, list.size()))));
		}
		return ret;
	}
	
	/**
	 * 调用setter，其特点是<br/>
	 * 1. 例如<code>void setFoo(TypeA)</code>方法，可以传入TypeA的任意子类作为参数。<br/>
	 * 2. 使用reflect.asm多次调用时性能较好
	 * 
	 * @throws IllegalArgumentException 当方法未找到或者类型不匹配时
	 * @param target 调用对象
	 * @param fName 属性名
	 * @param value 值
	 * @author ZhangXiaoye
	 * @date 2016年10月2日 下午6:20:36
	 */
	public static void setterInvoke(Object target, String fName, Object value){
		final MethodAccess ma = MethodAccess.get(target.getClass());
		final String setter = "set" + fName.substring(0, 1).toUpperCase() + fName.substring(1);
		int mi = ma.getIndex(setter);
		Class<?>[] parameterTypes = ma.getParameterTypes()[mi];
		if(parameterTypes.length == 1 && parameterTypes[0].isInstance(value)){
			ma.invoke(target, mi, value);
		}else{
			throw new IllegalArgumentException(String.format("unable to invoke %s(%s) with type %s.", setter, Arrays.toString(parameterTypes), value.getClass().getName()));
		}
	}
	
	/**
	 * 调用getter，其特点是<br/>
	 * 1. 使用reflect.asm多次调用时性能较好
	 * 
	 * @throws IllegalArgumentException 当方法未找到或者类型不匹配时
	 * @param target 调用对象
	 * @param fName 属性名
	 * @return 结果
	 * @author ZhangXiaoye
	 * @date 2016年10月2日 下午6:22:23
	 */
	public static <T> T getterInvoke(Object target, String fName){
		final MethodAccess ma = MethodAccess.get(target.getClass());
		String getter;
		int mi;
		try{
			getter = "get" + fName.substring(0, 1).toUpperCase() + fName.substring(1);
			mi = ma.getIndex(getter);
		}catch(IllegalArgumentException e){
			getter = "is" + fName.substring(0, 1).toUpperCase() + fName.substring(1);
			mi = ma.getIndex(getter);
		}
		@SuppressWarnings("unchecked")
		T ret = (T) ma.invoke(target, mi);
		return ret;
	}

	public static <T> boolean setFieldValueByReflect(Object target, String fName, Class<? super T> fType, T fValue){
		if (target == null || fName == null || "".equals(fName) || (fValue != null && !fType.isAssignableFrom(fValue.getClass()))) {
			return false;
		}
		Class<?> clazz = target.getClass();
		try {
			Method method = clazz.getDeclaredMethod("set" + Character.toUpperCase(fName.charAt(0)) + fName.substring(1), fType);
			if (!Modifier.isPublic(method.getModifiers())) {
				method.setAccessible(true);
			}
			method.invoke(target, fValue);
			return true;
		} catch (NoSuchMethodException e) {
			try {
				Field field = clazz.getDeclaredField(fName);
				if (!Modifier.isPublic(field.getModifiers())) {
					field.setAccessible(true);
				}
				field.set(target, fValue);
				return true;
			} catch (Exception fe) {
				if (logger.isDebugEnabled()) {
					logger.debug(fe.getMessage(), fe);
				}
			}
		} catch (Exception me) {
			if (logger.isDebugEnabled()) {
				logger.debug(me.getMessage(), me);
			}
		}
		return false;
	}
	
	@SuppressWarnings("unchecked")
	public static <T> T getFieldValueByReflect(Object target, String fName, Class<? extends T> fType){
		if (target == null || fName == null || "".equals(fName)) {
			return null;
		}
		Class<?> clazz = target.getClass();
		try {
			Method method = clazz.getDeclaredMethod("get" + Character.toUpperCase(fName.charAt(0)) + fName.substring(1));
			if(fType.isAssignableFrom(method.getReturnType())){
				if (!Modifier.isPublic(method.getModifiers())) {
					method.setAccessible(true);
				}
				return (T) method.invoke(target);
			}
		}catch (NoSuchMethodException e) {
			try {
				Field field = clazz.getDeclaredField(fName);
				if (!Modifier.isPublic(field.getModifiers())) {
					field.setAccessible(true);
				}
				if(fType.isAssignableFrom(field.getType())){
					return (T) field.get(target);
				}
			} catch (Exception fe) {
				if (logger.isDebugEnabled()) {
					logger.debug(fe.getMessage(), fe);
				}
			}
		}catch (Exception me){
			if (logger.isDebugEnabled()) {
				logger.debug(me.getMessage(), me);
			}
		}
		return null;
	}
	
	/**
	 * 对于集合类型的Field，如果是直接声明范型，返回范围类型，否则null。
	 * 
	 * @param field
	 * @return 集合类型属性直接声明的范型类型
	 * @throws NullPointerException field参数为null
	 * @author Zhang Xiaoye
	 * @date 2017年9月7日 上午11:04:03
	 */
	public static Class<?> getFieldGenericType(Field field){
		Objects.requireNonNull(field);
		if(Collection.class.isAssignableFrom(field.getType())){
			// 对于集合，获取其范型
			Type gType = field.getGenericType();
			if(gType instanceof ParameterizedType){
				// 只支持直接声明了范型类型的情况
				Type genericType = ((ParameterizedType) gType).getActualTypeArguments()[0];
				if(genericType instanceof Class){
					return ((Class<?>) genericType);
				}
			}
		}
		return null;
	}
	
	/**
	 * 获取方法参数的范型声明
	 * 
	 * @param method
	 * @param index 第几个参数，当心{@link IndexOutOfBoundsException}
	 * @return
	 * @author Zhang Xiaoye
	 * @date 2017年12月15日 下午8:10:49
	 */
	public static Class<?> getMethodParameterGenericType(Method method, int index){
		Objects.requireNonNull(method);
		Type gType = method.getGenericParameterTypes()[index];
		if(gType instanceof ParameterizedType){
			// 只支持直接声明了范型类型的情况
			Type genericType = ((ParameterizedType) gType).getActualTypeArguments()[0];
			if(genericType instanceof Class){
				return ((Class<?>) genericType);
			}
		}
		return null;
	}
	
	/**
	 * 获取能实例化的Collection实现类
	 * 
	 * @param type 例如List、Set等
	 * @return 能实例化的Collection实现类
	 * @author Zhang Xiaoye
	 * @date 2017年9月7日 上午11:23:43
	 */
	public static Class<?> getCollectionImplements(Class<?> type){
		Objects.requireNonNull(type);
		if(! Collection.class.isAssignableFrom(type)){
			return null;
		}
		for(Class<?> collType: new Class<?>[]{ArrayList.class, LinkedHashSet.class}){
			if(type.isAssignableFrom(collType)){
				return collType;
			}
		}
		try{
			type.getConstructor();
			return type;
		}catch(Exception e){
			return null;
		}
	}
	
	public static void setExceptionCause(Exception e, Exception cause){
		try{
			if(e != null && cause != null){
				Field cField = e.getClass().getField("cause");
				cField.setAccessible(true);
				cField.set(e, cause);
			}
		}catch(Exception ee){
			
		}
	}

}
