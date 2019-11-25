package com.tenchael.metrics.extension.metrics;

import com.tenchael.metrics.extension.reporter.jmx.JmxReporter;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

public class MetricsRegisteryTests extends Assert {

	private MetricsRegistery registery = MetricsRegistery.getInstance();
	private JmxReporter.JmxListener listener = new JmxReporter.JmxListener();

	@Before
	public void setUp() {
		registery.addListener(listener);
	}

	@Test
	public void testRegister_counter() throws Exception {
		Counter counter = registery.counter("metrics.count", "Total", "echo");
		counter.incr();
		assertTrue(true);
	}

	@Test
	public void testRegister_counter_url() throws Exception {
		String name = "/hello/world";
		Counter counter = registery.counter("metrics.count", "Total", name);
		Counter counter2 = registery.counter("metrics.count", "Total", "echo");
		counter.incr();
		counter2.incr(4);
		assertTrue(true);
	}

	@Test
	public void testRegister_histogram() throws Exception {
		Histogram histogram = registery.histogram("metrics.histogram", "Histogram", "someMethod");
		assertNotNull(histogram);
		Histogram.Context ctx = histogram.time();
		TimeUnit.MILLISECONDS.sleep(1);
		ctx.stop();
		assertTrue(true);
	}

}
