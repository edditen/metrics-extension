package com.tenchael.metrics.extension.reporter.logger;

import com.alibaba.fastjson.JSON;
import com.tenchael.metrics.extension.common.PropertiesManager;
import com.tenchael.metrics.extension.common.logger.Logger;
import com.tenchael.metrics.extension.common.logger.LoggerFactory;
import com.tenchael.metrics.extension.metrics.*;
import com.tenchael.metrics.extension.reporter.ScheduledReporter;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Created by Tenchael on 2020/2/24.
 */
public class LogReporter extends ScheduledReporter {

	public static final String REPORTER_INTERVAL_PROPS_KEY = "metrics.ext.reporter.interval";
	public static final long DEFAULT_INTERVAL_SECS = 60L;
	private static final Logger LOGGER = LoggerFactory.getLogger(LogReporter.class);
	private static final Logger METRICS_LOG = LoggerFactory.getLogger("metrics");

	public LogReporter() {
		this(new Long(PropertiesManager.getInstance().getProperties()
				.getOrDefault(REPORTER_INTERVAL_PROPS_KEY, DEFAULT_INTERVAL_SECS).toString()));
	}

	public LogReporter(long intervalSecs) {
		super(intervalSecs, TimeUnit.SECONDS);
	}

	@Override
	protected void report(Map<MetricKey, Metrics> metricsData) {
		if (metricsData == null) {
			LOGGER.warn("Metrics data is null");
			return;
		}

		Map<MetricKey, Histogram> histograms = metricsData.entrySet().stream()
				.filter(item -> item.getKey().getMetricType() == MetricKey.MetricType.histogram)
				.collect(Collectors.toMap(item -> item.getKey(), item -> (Histogram) item.getValue()));
		Map<MetricKey, Counter> counters = metricsData.entrySet().stream()
				.filter(item -> item.getKey().getMetricType() == MetricKey.MetricType.counter)
				.collect(Collectors.toMap(item -> item.getKey(), item -> (Counter) item.getValue()));
		reportHistograms(histograms);
		reportCounters(counters);

	}

	public void reportCounters(Map<MetricKey, Counter> counters) {
		Map<String, Long> formatCounters = counters.entrySet().stream()
				.collect(Collectors.toMap(item -> item.getKey().toString(),
						item -> item.getValue().getCount()));
		PageableHolder<String> pageableHolder = new PageableHolder<>(new ArrayList<>(formatCounters.keySet()));
		Iterator<List<String>> iterator = pageableHolder.iterator();
		while (iterator.hasNext()) {
			List<String> page = iterator.next();
			Map<String, Long> subCounterMap = subMap(formatCounters, page);
			METRICS_LOG.info(String.format("[COUNTER], %s",
					JSON.toJSONString(subCounterMap)));
		}

	}

	public void reportHistograms(Map<MetricKey, Histogram> histograms) {
		for (Map.Entry<MetricKey, Histogram> item : histograms.entrySet()) {
			Snapshot snapshot = item.getValue().getSnapshot();
			Map<String, Object> metricsMap = snapshotToMap(snapshot);
			metricsMap.putIfAbsent("name", item.getKey().toString());
			METRICS_LOG.info(String.format("[HISTOGRAM], %s",
					JSON.toJSONString(metricsMap)));
		}
	}

	public <T> Map<String, T> subMap(Map<String, T> map, List<String> keys) {
		Map<String, T> subMap = new HashMap<>();
		for (String key : keys) {
			if (map.containsKey(key)) {
				subMap.put(key, map.get(key));
			}
		}
		return subMap;
	}

	public Map<String, Object> snapshotToMap(Snapshot snapshot) {
		Map<String, Object> map = new HashMap<>();
		map.put("mean", snapshot.getMean());
		map.put("median", snapshot.getMedian());
		map.put("max", snapshot.getMax());
		map.put("min", snapshot.getMin());
		map.put("p75", snapshot.get75thPercentile());
		map.put("p95", snapshot.get95thPercentile());
		map.put("p98", snapshot.get98thPercentile());
		map.put("p99", snapshot.get99thPercentile());
		map.put("p999", snapshot.get999thPercentile());
		map.put("stddev", snapshot.getStdDev());
		return map;
	}


	@Override
	public void onCounterAdded(MetricKey key, Counter counter) {
		LOGGER.info(String.format("Add counter with key: %s", key));
	}

	@Override
	public void onCounterRemoved(MetricKey key) {
		LOGGER.info(String.format("Remove counter with key: %s", key));
	}

	@Override
	public void onHistogramAdded(MetricKey key, Histogram histogram) {
		LOGGER.info(String.format("Add histogram with key: %s", key));
	}

	@Override
	public void onHistogramRemoved(MetricKey key) {
		LOGGER.info(String.format("Remove histogram with key: %s", key));
	}
}
