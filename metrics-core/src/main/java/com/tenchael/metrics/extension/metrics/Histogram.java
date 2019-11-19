package com.tenchael.metrics.extension.metrics;

import java.io.Closeable;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class Histogram implements Metrics, Counting, Sampling {

    private final Reservoir reservoir;
    private final Counter counter;
    private final Counter u10Counter;
    private final Counter u50Counter;
    private final Counter u500Counter;

    private final long u10Nano = TimeUnit.MILLISECONDS.toNanos(10);
    private final long u50Nano = TimeUnit.MILLISECONDS.toNanos(50);
    private final long u500Nano = TimeUnit.MILLISECONDS.toNanos(500);

    public Histogram() {
        this(new UniformReservoir(), 0);
    }

    public Histogram(Reservoir reservoir) {
        this(reservoir, 0);
    }

    public Histogram(Reservoir reservoir, long initValue) {
        this.reservoir = reservoir;
        this.counter = new Counter(initValue);
        this.u10Counter = new Counter(0);
        this.u50Counter = new Counter(0);
        this.u500Counter = new Counter(0);
    }

    @Override
    public long getCount() {
        return counter.getCount();
    }

    public long getU10() {
        return u10Counter.getCount();
    }

    public long getU50() {
        return u50Counter.getCount();
    }

    public long getU500() {
        return u500Counter.getCount();
    }

    /**
     * add an elapse to histogram(duration: nano-seconds)
     *
     * @param value
     */
    public void update(long value) {
        update(value, TimeUnit.NANOSECONDS);
    }

    public void update(long value, TimeUnit timeUnit) {
        long nanoValue = timeUnit.toNanos(value);
        this.reservoir.update(nanoValue);
        this.counter.incr();
        if (nanoValue > u10Nano) {
            this.u10Counter.incr();
        }
        if (nanoValue > u50Nano) {
            this.u50Counter.incr();
        }
        if (nanoValue > u500Nano) {
            this.u500Counter.incr();
        }
    }

    public Context time() {
        return new Context(this, System.nanoTime());
    }

    @Override
    public Snapshot getSnapshot() {
        return reservoir.getSnapshot();
    }

    public static class Context implements Closeable {
        private Histogram histogram;
        private long startTime;

        public Context(Histogram histogram, long startTime) {
            this.histogram = histogram;
            this.startTime = startTime;
        }

        public long stop() {
            long elapse = nowTime() - startTime;
            histogram.update(elapse);
            return elapse;
        }

        private long nowTime() {
            return System.nanoTime();
        }

        @Override
        public void close() throws IOException {
            stop();
        }
    }
}
