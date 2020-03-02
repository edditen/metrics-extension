package com.tenchael.metrics.extension.common;

public interface SwallowExceptionListener {
	SwallowExceptionListener STDOUT = (message, e) -> {
		if (message != null) {
			System.err.println(message);
		}
		if (e != null) {
			e.printStackTrace();
		}
	};

	void onException(String message, Throwable e);


}
