package com.tenchael.metrics.extension.metrics;

import java.util.EventListener;

/**
 * Listeners for events from the registry.  Listeners must be thread-safe.
 *
 * <p>Copy from io.dropwizard.metrics:metrics-core</p>
 * * of  com.codahale.metrics.MetricRegistryListener.
 */
public interface MetricRegistryListener extends EventListener {

	/**
	 * Called when a {@link Counter} is added to the registry.
	 *
	 * @param name    the counter's name
	 * @param counter the counter
	 */
	void onCounterAdded(String name, Counter counter);

	/**
	 * Called when a {@link Counter} is removed from the registry.
	 *
	 * @param name the counter's name
	 */
	void onCounterRemoved(String name);

	/**
	 * Called when a {@link Histogram} is added to the registry.
	 *
	 * @param name      the histogram's name
	 * @param histogram the histogram
	 */
	void onHistogramAdded(String name, Histogram histogram);

	/**
	 * Called when a {@link Histogram} is removed from the registry.
	 *
	 * @param name the histogram's name
	 */
	void onHistogramRemoved(String name);

	/**
	 * A no-op implementation of {@link MetricRegistryListener}.
	 */
	abstract class Base implements MetricRegistryListener {
		@Override
		public void onCounterAdded(String name, Counter counter) {
		}

		@Override
		public void onCounterRemoved(String name) {
		}

		@Override
		public void onHistogramAdded(String name, Histogram histogram) {
		}

		@Override
		public void onHistogramRemoved(String name) {
		}


	}


}
