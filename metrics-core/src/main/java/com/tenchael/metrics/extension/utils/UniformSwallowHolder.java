package com.tenchael.metrics.extension.utils;

public class UniformSwallowHolder {
	private static SwallowExceptionListener listener = SwallowExceptionListener.STDOUT;

	public static SwallowExceptionListener getListener() {
		return UniformSwallowHolder.listener;
	}

	public static void setListener(SwallowExceptionListener listener) {
		UniformSwallowHolder.listener = listener;
	}
}