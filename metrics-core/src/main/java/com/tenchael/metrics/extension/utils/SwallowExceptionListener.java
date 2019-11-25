package com.tenchael.metrics.extension.utils;

public interface SwallowExceptionListener {
	SwallowExceptionListener STDOUT = (message, e) -> {
		if (message != null) {
			System.out.println(message);
		}
		if (e != null) {
			e.printStackTrace();
		}
	};

	void onException(String message, Exception e);


}
