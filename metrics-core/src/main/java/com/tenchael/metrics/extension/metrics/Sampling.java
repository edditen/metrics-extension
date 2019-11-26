package com.tenchael.metrics.extension.metrics;

/**
 * Sampling is used for getting snapshot of reservoir
 * Created by Tenchael on 2019/11/26.
 */
public interface Sampling {

	Snapshot getSnapshot();
}
