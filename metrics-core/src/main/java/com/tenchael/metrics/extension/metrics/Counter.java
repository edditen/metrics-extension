package com.tenchael.metrics.extension.metrics;

import java.util.concurrent.atomic.LongAdder;

public class Counter implements Metrics, Counting {

    private final LongAdder count;

    public Counter() {
        this(0);
    }

    public Counter(long initValue) {
        count = new LongAdder();
        count.add(initValue);
    }

    public void incr() {
        count.increment();
    }

    public void incr(long delta) {
        count.add(delta);
    }

    public void decr() {
        count.decrement();
    }

    public void decr(long delta) {
        count.add(-delta);
    }

    @Override
    public long getCount() {
        return count.sum();
    }

}
