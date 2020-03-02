package com.tenchael.metrics.extension.reporter.jmx;

import com.tenchael.metrics.extension.metrics.MetricKey;
import com.tenchael.metrics.extension.metrics.Metrics;
import com.tenchael.metrics.extension.metrics.MetricsRegistry;
import com.tenchael.metrics.extension.reporter.ScheduledReporter;
import org.junit.Assert;
import org.junit.Test;

import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by Tenchael on 2020/2/25.
 */
public class ScheduleReporterTestes extends Assert {


	@Test
	public void testScheduledReporter() throws Exception {
		MockReporter reporter = new MockReporter();
		assertNotNull(reporter);
		assertEquals(60, reporter.getInterval());
		assertEquals(MetricsRegistry.getInstance(), reporter.getMetricsRegistry());
		assertEquals(TimeUnit.SECONDS, reporter.getTimeUnit());
		assertNotNull(reporter.getExecutor());

	}

	@Test
	public void testScheduledReporter2() throws Exception {
		MockReporter reporter = new MockReporter(10);
		assertNotNull(reporter);
		assertEquals(0, reporter.getCount());
		TimeUnit.MILLISECONDS.sleep(100);
		int count = reporter.getCount();
		assertTrue(Math.abs(count - 9) <= 1);
		reporter.close();
	}

	static class MockReporter extends ScheduledReporter {

		private final AtomicInteger count = new AtomicInteger(0);

		public MockReporter() {
			super();
		}

		public MockReporter(long intervalMillis) {
			super(intervalMillis, TimeUnit.MILLISECONDS);
		}

		@Override
		protected void report(Map<MetricKey, Metrics> metricsData) {
			count.incrementAndGet();
		}

		public int getCount() {
			return count.get();
		}

		public long getInterval() {
			return interval;
		}

		public MetricsRegistry getMetricsRegistry() {
			return metricsRegistry;
		}

		public ScheduledExecutorService getExecutor() {
			return executor;
		}

		public TimeUnit getTimeUnit() {
			return timeUnit;
		}
	}

}
