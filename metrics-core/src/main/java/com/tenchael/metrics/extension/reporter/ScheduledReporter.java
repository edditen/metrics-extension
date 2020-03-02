package com.tenchael.metrics.extension.reporter;

import com.tenchael.metrics.extension.common.utils.NamedThreadFactory;
import com.tenchael.metrics.extension.metrics.MetricKey;
import com.tenchael.metrics.extension.metrics.Metrics;
import com.tenchael.metrics.extension.metrics.MetricsRegistry;

import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by Tenchael on 2020/2/25.
 */
public abstract class ScheduledReporter implements Reporter {

	public static final long DEFAULT_INTERVAL_SECS = 60L;
	protected final long interval;
	protected final MetricsRegistry metricsRegistry;
	protected final ScheduledExecutorService executor;
	protected final TimeUnit timeUnit;

	public ScheduledReporter() {
		this(DEFAULT_INTERVAL_SECS, TimeUnit.SECONDS);
	}

	public ScheduledReporter(long interval, TimeUnit timeUnit) {
		this(MetricsRegistry.getInstance(), interval, timeUnit);
	}

	public ScheduledReporter(MetricsRegistry metricsRegistry, long interval, TimeUnit timeUnit) {
		this(metricsRegistry,
				Executors.newSingleThreadScheduledExecutor(
						new NamedThreadFactory("metrics-schedule")),
				interval, timeUnit);
	}

	public ScheduledReporter(MetricsRegistry metricsRegistry, ScheduledExecutorService executor,
	                         long interval, TimeUnit timeUnit) {
		this.interval = interval;
		this.metricsRegistry = metricsRegistry;
		this.executor = executor;
		this.timeUnit = timeUnit;
		this.start();
	}

	@Override
	public void start() {
		this.executor.scheduleAtFixedRate(() -> {
			report(metricsRegistry.getMetricsData());
		}, this.interval, this.interval, this.timeUnit);

	}

	protected abstract void report(Map<MetricKey, Metrics> metricsData);

	@Override
	public void close() {
		this.executor.shutdown();
	}

}
