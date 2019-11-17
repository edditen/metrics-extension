package com.tenchael.metrics.extension.metrics;


import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicLongArray;

/**
 * A random sampling reservoir of a stream of {@code long}s. Uses Vitter's Algorithm R to produce a
 * statistically representative sample.
 * <p>Copy from io.dropwizard.metrics:metrics-core</p>
 * of com.codahale.metrics.UniformReservoir.
 *
 * @see <a href="https://metrics.dropwizard.io/3.1.0/getting-started">Metrics</a>
 */
public class UniformReservoir implements Reservoir {

    public static final int DEFAULT_SIZE = 1028;
    private static final int BITS_PER_LONG = 63;
    private final AtomicLong count = new AtomicLong(0);
    private final AtomicLongArray values;

    public UniformReservoir() {
        this(DEFAULT_SIZE);
    }


    public UniformReservoir(int size) {
        this.values = new AtomicLongArray(size);
        for (int i = 0; i < values.length(); i++) {
            values.set(i, 0);
        }
        count.set(0);
    }

    /**
     * Get a pseudo-random long uniformly between 0 and n-1. Stolen from
     * {@link java.util.Random#nextInt()}.
     *
     * @param n the bound
     * @return a value select randomly from the range {@code [0..n)}.
     */
    private static long nextLong(long n) {
        long bits, val;
        do {
            bits = ThreadLocalRandom.current().nextLong() & (~(1L << BITS_PER_LONG));
            val = bits % n;
        } while (bits - val + (n - 1) < 0L);
        return val;
    }

    @Override
    public int size() {
        final long c = count.get();
        if (c > values.length()) {
            return values.length();
        }
        return (int) c;
    }

    @Override
    public void update(long value) {
        final long c = count.incrementAndGet();
        if (c <= values.length()) {
            values.set((int) c - 1, value);
        } else {
            final long r = nextLong(c);
            if (r < values.length()) {
                values.set((int) r, value);
            }
        }
    }

    @Override
    public Snapshot getSnapshot() {
        final int s = size();
        final List<Long> copy = new ArrayList<Long>(s);
        for (int i = 0; i < s; i++) {
            copy.add(values.get(i));
        }
        return new UniformSnapshot(copy);
    }


}
