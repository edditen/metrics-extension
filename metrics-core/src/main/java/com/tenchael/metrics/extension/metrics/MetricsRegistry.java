package com.tenchael.metrics.extension.metrics;

import com.tenchael.metrics.extension.common.ExtensionLoader;
import com.tenchael.metrics.extension.common.logger.Logger;
import com.tenchael.metrics.extension.common.logger.LoggerFactory;
import com.tenchael.metrics.extension.common.utils.NamedThreadFactory;
import com.tenchael.metrics.extension.reporter.Reporter;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Metrics registry
 * Created by Tenchael on 2019/11/26.
 */
public class MetricsRegistry {
	private static final Logger LOGGER = LoggerFactory.getLogger(MetricsRegistry.class);
	private static volatile MetricsRegistry instance = new MetricsRegistry();
	private final ConcurrentHashMap<MetricKey, Metrics> metricsData;
	private final Set<MetricRegistryListener> listeners;
	private final Executor notifyExecutor;

	private MetricsRegistry() {
		this.metricsData = new ConcurrentHashMap<>();
		this.listeners = new HashSet<>();
		this.notifyExecutor = Executors
				.newCachedThreadPool(new NamedThreadFactory("notify"));
		Executors.newSingleThreadExecutor().execute(() -> {
			List<Reporter> reporters = ExtensionLoader
					.getExtensionLoader(Reporter.class)
					.getServices();
			reporters.forEach(reporter -> this.addListener(reporter));
		});
	}

	public static MetricsRegistry getInstance() {
		return instance;
	}

	public <T extends Metrics> T register(MetricKey key, T metric) {
		final Metrics existing = metricsData.putIfAbsent(key, metric);
		if (existing == null) {
			onMetricsAdded(key, metric);
			return metric;
		} else {
			throw new IllegalArgumentException(String.format("A metrics key %s already exists", key));
		}
	}

	public void unregister(MetricKey key) {
		boolean contains = metricsData.containsKey(key);
		if (!contains) {
			return;
		}
		metricsData.remove(key);
		onMetricsRemoved(key);
	}

	public void unregisterAll() {
		for (MetricKey key : metricsData.keySet()) {
			onMetricsRemoved(key);
		}
		metricsData.clear();
	}

	public Map<MetricKey, Metrics> getMetricsData() {
		return metricsData;
	}

	private void onMetricsAdded(MetricKey key, Metrics metric) {
		LOGGER.info(String.format("Add a metric with key: %s", key));
		for (MetricRegistryListener listener : listeners) {
			//async notify
			notifyExecutor.execute(() ->
					notifyListenerOfAddedMetrics(listener, key, metric)
			);
		}
	}

	private void onMetricsRemoved(MetricKey key) {
		LOGGER.info(String.format("Remove a metric with key: %s", key));
		for (MetricRegistryListener listener : listeners) {
			//async notify
			notifyExecutor.execute(() ->
					notifyListenerOfRemovedMetrics(listener, key)
			);
		}
	}

	private void notifyListenerOfAddedMetrics(MetricRegistryListener listener,
	                                          MetricKey key, Metrics metrics) {
		if (metrics instanceof Counter) {
			listener.onCounterAdded(key, (Counter) metrics);
		} else if (metrics instanceof Histogram) {
			listener.onHistogramAdded(key, (Histogram) metrics);
		} else {
			throw new IllegalArgumentException("Unknown metrics type: " + metrics.getClass());
		}
	}

	private void notifyListenerOfRemovedMetrics(MetricRegistryListener listener, MetricKey key) {
		if (key.getMetricType() == MetricKey.MetricType.counter) {
			listener.onCounterRemoved(key);
		} else if (key.getMetricType() == MetricKey.MetricType.histogram) {
			listener.onHistogramRemoved(key);
		} else {
			throw new IllegalArgumentException("Unknown metrics type: " + key.getMetricType());
		}
	}

	public Counter counter(MetricKey key) {
		key.setMetricType(MetricKey.MetricType.counter);
		return getOrAdd(key, MetricsBuilder.COUNTERS);
	}

	public Histogram histogram(MetricKey key) {
		key.setMetricType(MetricKey.MetricType.histogram);
		return getOrAdd(key, MetricsBuilder.HISTOGRAMS);
	}

	private <T extends Metrics> T getOrAdd(MetricKey key, MetricsBuilder<T> builder) {
		final Metrics metrics = getMetricsData().get(key);
		if (metrics == null) {
			try {
				return register(key, builder.newMetrics());
			} catch (IllegalArgumentException e) {
				//concurrent handle
				final Metrics added = metricsData.get(key);
				if (builder.isInstance(added)) {
					return (T) added;
				}
			}
		} else if (builder.isInstance(metrics)) {
			return (T) metrics;
		}
		//should not happened here
		throw new IllegalArgumentException(
				String.format("Thr %s is already used for a different type of metrics", key));
	}

	public void addListener(MetricRegistryListener listener) {
		if (listeners.contains(listener)) {
			return;
		}

		listeners.add(listener);

		for (Map.Entry<MetricKey, Metrics> entry : metricsData.entrySet()) {
			notifyListenerOfAddedMetrics(listener, entry.getKey(), entry.getValue());
		}
	}

	public void removeListener(MetricRegistryListener listener) {
		listeners.remove(listener);
	}


	private interface MetricsBuilder<T extends Metrics> {
		MetricsBuilder<Counter> COUNTERS = new MetricsBuilder<Counter>() {
			@Override
			public Counter newMetrics() {
				return new Counter();
			}

			@Override
			public boolean isInstance(Metrics metric) {
				return Counter.class.isInstance(metric);
			}
		};

		MetricsBuilder<Histogram> HISTOGRAMS = new MetricsBuilder<Histogram>() {
			@Override
			public Histogram newMetrics() {
				return new Histogram();
			}

			@Override
			public boolean isInstance(Metrics metric) {
				return Histogram.class.isInstance(metric);
			}
		};

		T newMetrics();

		boolean isInstance(Metrics metrics);
	}

}
