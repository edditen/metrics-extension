package com.tenchael.metrics.extension.metrics;

/**
 * Sampling is used for getting snapshot of reservoir
 */
public interface Sampling {

	Snapshot getSnapshot();
}
