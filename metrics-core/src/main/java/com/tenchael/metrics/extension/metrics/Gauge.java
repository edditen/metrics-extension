package com.tenchael.metrics.extension.metrics;

public class Gauge<T> implements Metrics {

    private volatile T value;

    public Gauge() {
        this(null);
    }

    public Gauge(T value) {
        this.value = value;
    }

    public T getValue() {
        return this.value;
    }

    public void setValue(T value) {
        this.value = value;
    }
}
