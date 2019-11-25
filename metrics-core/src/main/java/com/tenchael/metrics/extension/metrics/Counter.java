package com.tenchael.metrics.extension.metrics;

import java.util.concurrent.atomic.LongAdder;

/**
 * The implementation of counting, can increase or decrease by steps(delta)
 */
public class Counter implements Metrics, Counting {

	private final LongAdder count;

	public Counter() {
		this(0);
	}

	public Counter(long initValue) {
		count = new LongAdder();
		count.add(initValue);
	}

	/**
	 * count increase one every time
	 */
	public void incr() {
		count.increment();
	}

	/**
	 * count increase delta every time
	 *
	 * @param delta
	 */
	public void incr(long delta) {
		count.add(delta);
	}

	/**
	 * count decrease one every time
	 */
	public void decr() {
		count.decrement();
	}

	/**
	 * count decrease delta every time
	 *
	 * @param delta
	 */
	public void decr(long delta) {
		count.add(-delta);
	}

	/**
	 * current count value
	 *
	 * @return
	 */
	@Override
	public long getCount() {
		return count.sum();
	}

}
