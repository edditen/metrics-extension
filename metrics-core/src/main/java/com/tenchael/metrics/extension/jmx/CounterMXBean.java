package com.tenchael.metrics.extension.jmx;

public interface CounterMXBean extends MBean {

    long getCount();

    void incr();

    void incr(long delta);

    void decr();

    void decr(long delta);
}
