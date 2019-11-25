package com.tenchael.metrics.extension.metrics;

/**
 * A statistically representative reservoir of a data stream.
 * <p>Copy from io.dropwizard.metrics:metrics-core</p>
 * of  com.codahale.metrics.Reservoir.
 *
 * @see <a href="https://metrics.dropwizard.io/3.1.0/getting-started">Metrics</a>
 */
public interface Reservoir {


	/**
	 * Returns the number of values recorded.
	 *
	 * @return the number of values recorded
	 */
	int size();

	/**
	 * Adds a new recorded value to the reservoir.
	 *
	 * @param value a new recorded value
	 */
	void update(long value);

	/**
	 * Returns a snapshot of the reservoir's values.
	 *
	 * @return a snapshot of the reservoir's values
	 */
	Snapshot getSnapshot();
}
