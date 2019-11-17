package com.tenchael.metrics.extension.metrics;

import com.tenchael.metrics.extension.jmx.CounterMXBean;
import com.tenchael.metrics.extension.jmx.MBean.BaseMBean;

import java.util.concurrent.atomic.AtomicLong;

public class Counter extends BaseMBean implements CounterMXBean {

    private final AtomicLong count;

    public Counter(String category, String name) {
        this(category, name, 0);
    }

    public Counter(String category, String name, long initValue) {
        super(category, name);
        this.count = new AtomicLong(initValue);
    }


    @Override
    public long getCount() {
        return this.count.get();
    }

    @Override
    public void incr() {
        this.count.incrementAndGet();
    }

    @Override
    public void incr(long delta) {
        this.count.addAndGet(delta);
    }

    @Override
    public void decr() {
        this.count.decrementAndGet();
    }

    @Override
    public void decr(long delta) {
        this.count.addAndGet(-delta);
    }
}
