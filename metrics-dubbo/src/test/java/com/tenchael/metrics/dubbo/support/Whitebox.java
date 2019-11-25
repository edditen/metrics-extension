package com.tenchael.metrics.dubbo.support;

import java.lang.reflect.Field;

public class Whitebox {

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

}
