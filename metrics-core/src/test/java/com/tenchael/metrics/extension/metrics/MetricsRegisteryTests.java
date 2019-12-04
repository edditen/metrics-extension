package com.tenchael.metrics.extension.metrics;

import com.tenchael.metrics.extension.reporter.jmx.JmxReporter;
import com.tenchael.metrics.extension.support.Whitebox;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

import static org.mockito.Mockito.spy;

public class MetricsRegisteryTests extends Assert {

	private MetricsRegistry registery = MetricsRegistry.getInstance();
	private JmxReporter.JmxListener listener = new JmxReporter.JmxListener();

	@Before
	public void setUp() {
		registery.addListener(listener);
	}

	@Test
	public void testRegister() throws Exception {
		MetricsRegistry metricsRegistry = spy(Whitebox.newInstance(MetricsRegistry.class));
		Counter counter = new Counter();
		Counter ret = metricsRegistry.register("com.tenchael.metrics.extension.metrics",
				"Counter", "testRegister", counter);
		assertNotNull(ret);
		assertEquals(counter, ret);

	}

	@Test(expected = IllegalArgumentException.class)
	public void testRegister_duplicated() throws Exception {
		MetricsRegistry metricsRegistry = spy(Whitebox.newInstance(MetricsRegistry.class));
		Counter counter = new Counter();
		Counter ret = metricsRegistry.register("com.tenchael.metrics.extension.metrics",
				"Counter", "testRegister_duplicated", counter);
		assertNotNull(ret);
		try {
			metricsRegistry.register("com.tenchael.metrics.extension.metrics",
					"Counter", "testRegister_duplicated", counter);
			assertFalse(true);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}

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


	@Test
	public void testAddListener() throws Exception {
//		MetricsRegistry metricsRegistry = spy(Whitebox.newInstance(MetricsRegistry.class));
		MetricRegistryListener listener = new MetricRegistryListener.Base() {
		};
		registery.addListener(listener);
		registery.addListener(listener);
		assertTrue(true);
	}

	@Test
	public void testRemoveListener() throws Exception {
//		MetricsRegistry metricsRegistry = spy(Whitebox.newInstance(MetricsRegistry.class));
		MetricRegistryListener listener = new MetricRegistryListener.Base() {
		};
		registery.addListener(listener);
		registery.removeListener(listener);
		assertTrue(true);
	}

}
