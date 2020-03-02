package com.tenchael.metrics.extension.metrics;

import static java.lang.Math.min;

/**
 * <p>A sliding Window Reservoir.</p>
 * <p>Copy from io.dropwizard.metrics:metrics-core </p>
 * of com.codahale.metrics.SlidingWindowReservoir.
 *
 * @see <a href="https://metrics.dropwizard.io/3.1.0/getting-started">Metrics</a>
 */
public class SlidingWindowReservoir implements Reservoir {

	private static final int DEFAULT_SIZE = 1028;

	private final long[] measurements;
	private long count;

	public SlidingWindowReservoir() {
		this(DEFAULT_SIZE);
	}

	/**
	 * Creates a new {@link SlidingWindowReservoir} which stores the last {@code size} measurements.
	 *
	 * @param size the number of measurements to store
	 */
	public SlidingWindowReservoir(int size) {
		this.measurements = new long[size];
		this.count = 0;
	}

	@Override
	public synchronized int size() {
		return (int) min(count, measurements.length);
	}

	@Override
	public synchronized void update(long value) {
		measurements[(int) (count++ % measurements.length)] = value;
	}

	@Override
	public Snapshot getSnapshot() {
		final long[] values = new long[size()];
		for (int i = 0; i < values.length; i++) {
			synchronized (this) {
				values[i] = measurements[i];
			}
		}
		return new UniformSnapshot(values);
	}
}
