package com.tenchael.metrics.extension.utils;

/**
 * Uniform swallow listener holder, using stdout log for default
 * Created by Tenchael on 2019/11/26.
 */
public class UniformSwallowHolder {
	private static SwallowExceptionListener listener = SwallowExceptionListener.STDOUT;

	public static SwallowExceptionListener getListener() {
		return UniformSwallowHolder.listener;
	}

	public static void setListener(SwallowExceptionListener listener) {
		UniformSwallowHolder.listener = listener;
	}
}