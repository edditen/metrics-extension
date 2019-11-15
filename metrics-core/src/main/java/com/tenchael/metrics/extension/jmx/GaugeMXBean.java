package com.tenchael.metrics.extension.jmx;

public interface GaugeMXBean<T> extends MBean {

    T getValue();
}
