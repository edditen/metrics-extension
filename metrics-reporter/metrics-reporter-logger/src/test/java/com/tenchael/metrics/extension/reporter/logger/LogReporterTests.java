package com.tenchael.metrics.extension.reporter.logger;

import com.tenchael.metrics.extension.metrics.Histogram;
import com.tenchael.metrics.extension.metrics.MetricKey;
import com.tenchael.metrics.extension.metrics.MetricsRegistry;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by Tenchael on 2020/2/26.
 */
public class LogReporterTests extends Assert {

	@Test
	public void testReport() throws InterruptedException {
		LogReporter reporter = new LogReporter(1);
		MetricsRegistry metricsRegistry = MetricsRegistry.getInstance();
		MetricKey key = MetricKey.newBuilder()
				.category("Test")
				.name("testReport")
				.build();
		MetricKey key2 = MetricKey.newBuilder()
				.category("Test")
				.name("testReport2")
				.build();
		MetricKey key3 = MetricKey.newBuilder()
				.category("Test")
				.name("testReport3")
				.build();
		MetricKey key4 = MetricKey.newBuilder()
				.category("Test")
				.name("testReport4")
				.build();
		Histogram.Context ctx = metricsRegistry.histogram(key3).time();
		Histogram.Context ctx2 = metricsRegistry.histogram(key4).time();
		metricsRegistry.counter(key).incr();
		metricsRegistry.counter(key).incr();
		metricsRegistry.counter(key2).incr();
		metricsRegistry.counter(key2).incr();
		ctx.stop();
		ctx2.stop();
		assertNotNull(reporter);
//		TimeUnit.SECONDS.sleep(5);
	}
}
