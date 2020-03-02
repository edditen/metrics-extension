package com.tenchael.metrics.extension.metrics;

import java.util.EventListener;

/**
 * Listeners for events from the registry.  Listeners must be thread-safe.
 * <p>
 * <p>Copy from io.dropwizard.metrics:metrics-core</p>
 * * of  com.codahale.metrics.MetricRegistryListener.
 */
public interface MetricRegistryListener extends EventListener {

	/**
	 * Called when a {@link Counter} is added to the registry.
	 *
	 * @param key     the counter's key
	 * @param counter the counter
	 */
	default void onCounterAdded(MetricKey key, Counter counter) {
	}

	/**
	 * Called when a {@link Counter} is removed from the registry.
	 *
	 * @param key the counter's key
	 */
	default void onCounterRemoved(MetricKey key) {
	}

	/**
	 * Called when a {@link Histogram} is added to the registry.
	 *
	 * @param key       the histogram's key
	 * @param histogram the histogram
	 */
	default void onHistogramAdded(MetricKey key, Histogram histogram) {
	}

	/**
	 * Called when a {@link Histogram} is removed from the registry.
	 *
	 * @param key the histogram's key
	 */
	default void onHistogramRemoved(MetricKey key) {
	}

}
