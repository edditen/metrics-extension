package com.tenchael.metrics.extension.common.utils;

/**
 * Created by Tenchael on 2020/2/25.
 */

/**
 * 参数校验器
 * Created by Tenchael on 2018/4/16.
 */
public class Validator {

	private Validator() throws IllegalAccessException {
		throw new IllegalAccessException("Illegal access!");
	}

	/**
	 * 表达式是否为true
	 *
	 * @param expression
	 * @param message
	 * @param values
	 */
	public static void isTrue(final boolean expression, final String message, final Object... values) {
		if (expression == false) {
			throw new IllegalArgumentException(String.format(message, values));
		}
	}


	/**
	 * 校验非空
	 *
	 * @param object
	 * @param message
	 * @param values
	 * @param <T>
	 * @return
	 */
	public static <T> T notNull(final T object, final String message, final Object... values) {
		if (object == null) {
			throw new IllegalArgumentException(String.format(message, values));
		}
		return object;
	}
}
