package com.tenchael.metrics.extension.metrics;

import com.tenchael.metrics.extension.support.Whitebox;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

import static org.mockito.Mockito.spy;

public class MetricsRegisteryTests extends Assert {

	@Before
	public void setUp() {

	}

	@Test
	public void testRegister() throws Exception {
		MetricsRegistry metricsRegistry = spy(Whitebox.newInstance(MetricsRegistry.class));
		Counter counter = new Counter();
		MetricKey key = MetricKey.newBuilder()
				.metricType(MetricKey.MetricType.counter)
				.category("Invokes")
				.name("127.0.0.1")
				.build();
		Counter ret = metricsRegistry.register(key, counter);
		assertNotNull(ret);
		assertEquals(counter, ret);

	}

	@Test(expected = IllegalArgumentException.class)
	public void testRegister_duplicated() throws Exception {
		MetricsRegistry metricsRegistry = spy(Whitebox.newInstance(MetricsRegistry.class));
		Counter counter = new Counter();
		MetricKey key = MetricKey.newBuilder()
				.metricType(MetricKey.MetricType.counter)
				.category("Invokes")
				.name("127.0.0.1")
				.build();
		Counter ret = metricsRegistry.register(key, counter);
		assertNotNull(ret);
		try {
			MetricKey key2 = MetricKey.newBuilder()
					.metricType(MetricKey.MetricType.counter)
					.category("Invokes")
					.name("127.0.0.1")
					.build();
			metricsRegistry.register(key2, counter);
			assertFalse(true);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}

	}

	@Test
	public void testRegister_counter() throws Exception {
		MetricsRegistry metricsRegistry = spy(Whitebox.newInstance(MetricsRegistry.class));
		MetricKey key = MetricKey.newBuilder()
				.metricType(MetricKey.MetricType.counter)
				.category("Invokes")
				.name("127.0.0.1")
				.build();
		Counter counter = metricsRegistry.counter(key);
		counter.incr();
		assertEquals(1L, counter.getCount());
	}

	@Test
	public void testRegister_counter_url() throws Exception {
		MetricsRegistry metricsRegistry = spy(Whitebox.newInstance(MetricsRegistry.class));
		MetricKey key = MetricKey.newBuilder()
				.metricType(MetricKey.MetricType.counter)
				.category("Invokes")
				.name("/hello/world")
				.build();
		Counter counter = metricsRegistry.counter(key);

		MetricKey key2 = MetricKey.newBuilder()
				.metricType(MetricKey.MetricType.counter)
				.category("Invokes")
				.name("echo")
				.build();
		Counter counter2 = metricsRegistry.counter(key2);
		counter.incr();
		counter2.incr(4);
		assertEquals(1L, counter.getCount());
		assertEquals(4L, counter2.getCount());
	}

	@Test
	public void testRegister_histogram() throws Exception {
		MetricsRegistry metricsRegistry = spy(Whitebox.newInstance(MetricsRegistry.class));
		MetricKey key = MetricKey.newBuilder()
				.metricType(MetricKey.MetricType.histogram)
				.category("Elapse")
				.name("pkg.HelloService.someMethod")
				.build();
		Histogram histogram = metricsRegistry.histogram(key);
		assertNotNull(histogram);
		Histogram.Context ctx = histogram.time();
		TimeUnit.MILLISECONDS.sleep(1);
		ctx.stop();
		assertTrue(true);
	}


	@Test
	public void testAddListener() throws Exception {
//		MetricsRegistry metricsRegistry = spy(Whitebox.newInstance(MetricsRegistry.class));
		MetricsRegistry registery = spy(Whitebox.newInstance(MetricsRegistry.class));
		MetricRegistryListener listener = new MetricRegistryListener() {
		};
		registery.addListener(listener);
		assertTrue(true);
	}

	@Test
	public void testAddListener_withMetrics() throws Exception {
//		MetricsRegistry metricsRegistry = spy(Whitebox.newInstance(MetricsRegistry.class));
		MetricsRegistry registery = spy(Whitebox.newInstance(MetricsRegistry.class));
		MetricKey key1 = MetricKey.newBuilder()
				.metricType(MetricKey.MetricType.counter)
				.category("Test")
				.name("testAddListener_withMetrics")
				.build();
		registery.counter(key1);
		MetricKey key2 = MetricKey.newBuilder()
				.category("Test")
				.name("testAddListener_withMetrics")
				.build();
		registery.histogram(key2);
		MetricRegistryListener listener = new MetricRegistryListener() {
		};
		registery.addListener(listener);
		assertTrue(true);
	}

	@Test
	public void testRemoveListener() throws Exception {
		MetricsRegistry registery = MetricsRegistry.getInstance();
		MetricRegistryListener listener = new MetricRegistryListener() {
		};
		registery.addListener(listener);
		registery.removeListener(listener);
		assertTrue(true);
	}


	@Test
	public void testRemoveListener2() throws Exception {
		MetricsRegistry registery = MetricsRegistry.getInstance();
		MetricRegistryListener listener = new MetricRegistryListener() {
		};
		registery.addListener(listener);
		registery.addListener(listener);
		registery.removeListener(listener);
		assertTrue(true);
	}

	@Test
	public void testRemoveListener3() throws Exception {
		MetricsRegistry registery = MetricsRegistry.getInstance();
		MetricRegistryListener listener = new MetricRegistryListener() {
		};
		registery.addListener(listener);
		registery.addListener(new MetricRegistryListener() {
		});

		MetricKey key = new MetricKey();
		registery.counter(key);
		assertTrue(true);
	}


	@Test
	public void testUnregister() throws Exception {
		MetricsRegistry registery = MetricsRegistry.getInstance();
		registery.getMetricsData().clear();
		MetricKey key = MetricKey.newBuilder()
				.metricType(MetricKey.MetricType.counter)
				.category("Test")
				.name("testUnregister")
				.build();
		MetricKey key2 = MetricKey.newBuilder()
				.metricType(MetricKey.MetricType.histogram)
				.category("Test")
				.name("testUnregister2")
				.build();
		MetricKey key3 = MetricKey.newBuilder()
				.metricType(MetricKey.MetricType.counter)
				.category("Test")
				.name("testUnregister3")
				.build();
		registery.addListener(new MetricRegistryListener() {
		});
		registery.counter(key);
		registery.counter(key2);
		registery.counter(key3);
		registery.unregister(key);
		registery.unregister(key);
		registery.unregister(key2);
		assertEquals(1, registery.getMetricsData().size());

		registery.unregisterAll();
		assertEquals(0, registery.getMetricsData().size());
	}
}
