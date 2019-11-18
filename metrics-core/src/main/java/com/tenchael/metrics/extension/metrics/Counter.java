package com.tenchael.metrics.extension.metrics;

import java.util.concurrent.atomic.AtomicLong;

public class Counter implements Metrics {

    private final AtomicLong count;

    public Counter() {
        this(0);
    }

    public Counter(long initValue) {
        count = new AtomicLong(initValue);
    }

    public void incr() {
        count.incrementAndGet();
    }

    public void incr(long delta) {
        count.addAndGet(delta);
    }

    public void decr() {
        count.decrementAndGet();
    }

    public void decr(long delta) {
        count.addAndGet(-delta);
    }

    public long getCount() {
        return count.get();
    }

}
