package com.tenchael.metrics.extension.metrics;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class MetricsRegistery {
    private final ConcurrentMap<String, Metrics> metricsMap;

    private final Set<MetricRegistryListener> listeners;
    private final NameFactory nameFactory;

    private static volatile MetricsRegistery instance = new MetricsRegistery();

    private MetricsRegistery() {
        this.metricsMap = new ConcurrentHashMap<>();
        this.listeners = new HashSet<>();
        this.nameFactory = new NameFactory.DefaultNameFactory();
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
            throw new IllegalArgumentException("A metric named " + name + " already exists");
        }
    }

    private String metricsKey(String domain, String type, String name) {
        return nameFactory.createName(domain, type, name);
    }

    private void onMetricsAdded(String key, Metrics metric) {
        for (MetricRegistryListener listener : listeners) {
            notifyListenerOfAddedMetrics(listener, key, metric);
        }
    }

    private void notifyListenerOfAddedMetrics(MetricRegistryListener listener,
                                              String key, Metrics metric) {
        if (metric instanceof Gauge) {
            listener.onGaugeAdded(key, (Gauge<?>) metric);
        } else if (metric instanceof Counter) {
            listener.onCounterAdded(key, (Counter) metric);
        } else if (metric instanceof Histogram) {
            listener.onHistogramAdded(key, (Histogram) metric);
        } else {
            throw new IllegalArgumentException("Unknown metric type: " + metric.getClass());
        }
    }

    public Gauge gauge(String domain, String type, String name) {
        return getOrAdd(domain, type, name, MetricsBuilder.GAUGES);
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
                final Metrics added = metricsMap.get(name);
                if (builder.isInstance(added)) {
                    return (T) added;
                }
            }
        } else if (builder.isInstance(metrics)) {
            return (T) metrics;
        }
        throw new IllegalArgumentException(name + " is already used for a different type of metric");
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
        MetricsBuilder<Gauge> GAUGES = new MetricsBuilder<Gauge>() {
            @Override
            public Gauge newMetrics() {
                return new Gauge();
            }

            @Override
            public boolean isInstance(Metrics metrics) {
                return Gauge.class.isInstance(metrics);
            }
        };

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
                return new Histogram(new UniformReservoir());
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
