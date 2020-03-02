package com.tenchael.metrics.extension.reporter;

import com.tenchael.metrics.extension.metrics.MetricRegistryListener;


/**
 * Reporter for metrics result
 * Created by Tenchael on 2019/11/26.
 */
public interface Reporter extends MetricRegistryListener {

	default void start() {
	}

	default void close() {
	}
}
