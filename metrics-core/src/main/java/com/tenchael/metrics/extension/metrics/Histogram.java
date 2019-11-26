package com.tenchael.metrics.extension.metrics;

import java.io.Closeable;
import java.util.concurrent.TimeUnit;

/**
 * A histogram is used for getting the distribution of elapse
 * Created by Tenchael on 2019/11/26.
 */
public class Histogram implements Metrics, Counting, Sampling {

	/**
	 * record the elapse
	 */
	private final Reservoir reservoir;
	/**
	 * count for invocation
	 */
	private final Counter counter;
	/**
	 * count for elapse larger than 10 mills seconds
	 */
	private final Counter u10Counter;
	/**
	 * count for elapse larger than 50 mills seconds
	 */
	private final Counter u50Counter;
	/**
	 * count for elapse larger than 500 mills seconds
	 */
	private final Counter u500Counter;

	private final long u10Nano = TimeUnit.MILLISECONDS.toNanos(10);
	private final long u50Nano = TimeUnit.MILLISECONDS.toNanos(50);
	private final long u500Nano = TimeUnit.MILLISECONDS.toNanos(500);

	public Histogram() {
		this(new UniformReservoir());
	}

	public Histogram(Reservoir reservoir) {
		this(reservoir, 0);
	}

	public Histogram(Reservoir reservoir, long initValue) {
		this.reservoir = reservoir;
		this.counter = new Counter(initValue);
		this.u10Counter = new Counter();
		this.u50Counter = new Counter();
		this.u500Counter = new Counter();
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

	/**
	 * add an elapse to histogram
	 *
	 * @param value
	 * @param timeUnit duration
	 */
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

	/**
	 * get an context of current time
	 *
	 * @return
	 */
	public Context time() {
		return new Context(this);
	}

	@Override
	public Snapshot getSnapshot() {
		return reservoir.getSnapshot();
	}

	public static class Context implements Closeable {
		private final Histogram histogram;
		private final long startTime;

		public Context(Histogram histogram) {
			this.histogram = histogram;
			this.startTime = nowTime();
		}

		public long stop() {
			long elapse = nowTime() - startTime;
			histogram.update(elapse);
			return elapse;
		}

		/**
		 * now time with duration of nano seconds
		 *
		 * @return
		 */
		public long nowTime() {
			return System.nanoTime();
		}

		@Override
		public void close() {
			stop();
		}
	}
}
