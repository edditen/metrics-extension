package com.tenchael.metrics.extension.utils;

public interface SwallowExceptionListener {
    void onException(String message, Exception e);

    SwallowExceptionListener STDOUT = (message, e) -> {
        System.out.println(message);
        if (e != null) {
            e.printStackTrace();
        }
    };


}
