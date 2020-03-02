package com.tenchael.metrics.http.support;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class Whitebox {

	public static <T> T newInstance(Class<T> clazz) throws Exception {
		Constructor<T> constructor = (Constructor<T>) clazz
				.getDeclaredConstructors()[0];
		constructor.setAccessible(true);
		return constructor.newInstance();
	}

	/**
	 * 修改对象内部属性
	 *
	 * @param target
	 * @param fieldName
	 * @param value
	 */
	public static void setInternalState(Object target, String fieldName, Object value) {
		try {
			Field field = target.getClass().getDeclaredField(fieldName);
			field.setAccessible(true);
			field.set(target, value);
		} catch (Exception e) {
			throw new RuntimeException(String.format("can not change value of %s against target %s", fieldName, target), e);
		}

	}

	/**
	 * 获取对象内部属性
	 *
	 * @param target
	 * @param fieldName
	 */
	public static Object getInternalState(Object target, String fieldName) {
		try {
			Field field = target.getClass().getDeclaredField(fieldName);
			field.setAccessible(true);
			return field.get(target);
		} catch (Exception e) {
			throw new RuntimeException(String.format("can not get value of %s against target %s", fieldName, target), e);
		}
	}

	public static Object invokeMethod(Object target, String methodName, Object... params) {
		try {
			Class<?>[] paramClass = new Class[params.length];
			for (int i = 0; i < params.length; i++) {
				paramClass[i] = params[i].getClass();
			}
			Method method = target.getClass().getMethod(methodName, paramClass);
			method.setAccessible(true);
			return method.invoke(target, params);
		} catch (Exception e) {
			throw new RuntimeException(String.format("can not get value of %s against target %s", methodName, target), e);
		}
	}

}
