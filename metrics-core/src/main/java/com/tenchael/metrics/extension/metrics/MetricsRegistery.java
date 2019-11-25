package com.tenchael.metrics.extension.metrics;

import com.tenchael.metrics.extension.utils.NamedThreadFactory;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class MetricsRegistery {
	private static volatile MetricsRegistery instance = new MetricsRegistery();
	private final ConcurrentMap<String, Metrics> metricsMap;
	private final Set<MetricRegistryListener> listeners;
	private final NameFactory nameFactory;
	private final Executor notifyExecutor;

	private MetricsRegistery() {
		this.metricsMap = new ConcurrentHashMap<>();
		this.listeners = new HashSet<>();
		this.nameFactory = new NameFactory.DefaultNameFactory();
		this.notifyExecutor = Executors
				.newCachedThreadPool(new NamedThreadFactory("notify"));
	}

	public static MetricsRegistery getInstance() {
		return instance;
	}

	public <T extends Metrics> T register(String domain, String type, String name, T metric) {
		String key = metricsKey(domain, type, name);
		final Metrics existing = metricsMap.putIfAbsent(key, metric);
		if (existing == null) {
			onMetricsAdded(key, metric);
			return metric;
		} else {
			throw new IllegalArgumentException("A metrics named " + name + " already exists");
		}
	}

	private String metricsKey(String domain, String type, String name) {
		return nameFactory.createName(domain, type, name);
	}

	private void onMetricsAdded(String key, Metrics metric) {
		for (MetricRegistryListener listener : listeners) {
			//async notify
			notifyExecutor.execute(() ->
					notifyListenerOfAddedMetrics(listener, key, metric)
			);
		}
	}

	private void notifyListenerOfAddedMetrics(MetricRegistryListener listener,
	                                          String key, Metrics metrics) {
		if (metrics instanceof Counter) {
			listener.onCounterAdded(key, (Counter) metrics);
		} else if (metrics instanceof Histogram) {
			listener.onHistogramAdded(key, (Histogram) metrics);
		} else {
			throw new IllegalArgumentException("Unknown metrics type: " + metrics.getClass());
		}
	}

	public Counter counter(String domain, String type, String name) {
		return getOrAdd(domain, type, name, MetricsBuilder.COUNTERS);
	}

	public Histogram histogram(String domain, String type, String name) {
		return getOrAdd(domain, type, name, MetricsBuilder.HISTOGRAMS);
	}

	private <T extends Metrics> T getOrAdd(String domain, String type, String name, MetricsBuilder<T> builder) {
		String key = metricsKey(domain, type, name);
		final Metrics metrics = metricsMap.get(key);
		if (metrics == null) {
			try {
				return register(domain, type, name, builder.newMetrics());
			} catch (IllegalArgumentException e) {
				//concurrent handle
				final Metrics added = metricsMap.get(name);
				if (builder.isInstance(added)) {
					return (T) added;
				}
			}
		} else if (builder.isInstance(metrics)) {
			return (T) metrics;
		}
		//should not happened here
		throw new IllegalArgumentException(key + " is already used for a different type of metrics");
	}

	public void addListener(MetricRegistryListener listener) {
		if (listeners.contains(listener)) {
			return;
		}

		listeners.add(listener);

		for (Map.Entry<String, Metrics> entry : metricsMap.entrySet()) {
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
