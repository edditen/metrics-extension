package com.tenchael.metrics.extension.metrics;

import java.util.concurrent.atomic.AtomicLong;

public class Histogram implements Metrics {

    private final Reservoir reservoir;
    private final AtomicLong count;

    public Histogram() {
        this(new UniformReservoir(), 0);
    }

    public Histogram(Reservoir reservoir) {
        this(reservoir, 0);
    }

    public Histogram(Reservoir reservoir, long initValue) {
        this.reservoir = reservoir;
        this.count = new AtomicLong(initValue);
    }

    public long getCount() {
        return count.get();
    }

    public void update(long value) {
        this.count.incrementAndGet();
        this.reservoir.update(value);
    }

    public Snapshot getSnapshot() {
        return reservoir.getSnapshot();
    }
}
