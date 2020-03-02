package com.tenchael.metrics.extension.reporter.jmx;

import com.tenchael.jmx.ext.MBeanRegistry;
import com.tenchael.metrics.extension.common.SwallowExceptionListener;
import com.tenchael.metrics.extension.common.UniformSwallowHolder;
import com.tenchael.metrics.extension.metrics.Counter;
import com.tenchael.metrics.extension.metrics.Histogram;
import com.tenchael.metrics.extension.metrics.MetricKey;
import com.tenchael.metrics.extension.metrics.MetricRegistryListener;
import com.tenchael.metrics.extension.reporter.Reporter;
import com.tenchael.metrics.extension.support.Whitebox;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import java.io.IOException;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class JmxReporterTests {

	private SwallowExceptionListener originalListener;


	@Before
	public void setUp() {
		originalListener = UniformSwallowHolder.getListener();
	}

	@After
	public void tearDown() {
		UniformSwallowHolder.setListener(originalListener);
	}

	@Test
	public void testJmxReporter() throws Exception {
		Reporter reporter = new JmxReporter();
		assertNotNull(reporter);
	}

	@Test
	public void testJmxListener() {
		JmxReporter reporter = new JmxReporter();
		Object registry = Whitebox.getInternalState(reporter, "mBeanRegistry");
		assertEquals(MBeanRegistry.getInstance(), registry);
	}

	@Test
	public void testCreateName() throws Exception {
		MBeanRegistry mBeanRegistry = spy(Whitebox.newInstance(MBeanRegistry.class));
		JmxReporter reporter = Mockito.spy(new JmxReporter(mBeanRegistry));
		MetricKey key = MetricKey.newBuilder()
				.metricType(MetricKey.MetricType.counter)
				.category("invokes")
				.name("127.0.0.1")
				.build();
		ObjectName oname = reporter.createOName(key);
		System.out.println(oname);
		assertNotNull(oname);
//		assertEquals(oname.toString());
	}

	@Test
	public void testOnCounterAdded() throws Exception {
		//data preparation
		MBeanRegistry mBeanRegistry = spy(Whitebox.newInstance(MBeanRegistry.class));
		JmxReporter reporter = Mockito.spy(new JmxReporter(mBeanRegistry));

		MetricKey key = MetricKey.newBuilder()
				.metricType(MetricKey.MetricType.counter)
				.category("Invokes")
				.name("testOnCounterAdded")
				.build();
		//mock
		doNothing().when(mBeanRegistry).register(any(ObjectName.class), any(JmxCounterMXBean.JmxCounter.JmxCounter.class));

		// execution
		reporter.onCounterAdded(key, new Counter());


		//assert
		assertTrue(true);
	}

	@Test
	public void testOnCounterAdded_null() throws Exception {
		//data preparation
		MBeanRegistry mBeanRegistry = spy(Whitebox.newInstance(MBeanRegistry.class));
		JmxReporter reporter = Mockito.spy(new JmxReporter(mBeanRegistry));
		// execution
		MetricKey key = MetricKey.newBuilder()
				.metricType(MetricKey.MetricType.counter)
				.category("Invokes")
				.name("testOnCounterAdded_null")
				.build();
		reporter.onCounterAdded(key, null);


		//assert
		assertTrue(true);
	}

	@Test
	public void testOnCounterAdded_MalformedObjectNameException() throws Exception {
		//data preparation
		JmxReporter reporter = Mockito.spy(new JmxReporter());
		MetricKey key = MetricKey.newBuilder()
				.metricType(MetricKey.MetricType.counter)
				.category("Invokes")
				.name("testOnCounterAdded_MalformedObjectNameException")
				.build();

		//mock
		doThrow(MalformedObjectNameException.class).when(reporter).createOName(key);
		UniformSwallowHolder.setListener(new SwallowExceptionListener() {
			@Override
			public void onException(String message, Throwable e) {
				assertTrue(e instanceof MalformedObjectNameException);
			}
		});


		// execution
		reporter.onCounterAdded(key, new Counter());


		//assert
		assertTrue(true);
	}

	@Test
	public void testOnCounterAdded_Exception() throws Exception {
		//data preparation
		JmxReporter reporter = Mockito.spy(new JmxReporter());
		MetricKey key = MetricKey.newBuilder()
				.metricType(MetricKey.MetricType.counter)
				.category("Invokes")
				.name("testOnCounterAdded_Exception")
				.build();
		//mock
		doThrow(NullPointerException.class).when(reporter).createOName(key);
		UniformSwallowHolder.setListener(new SwallowExceptionListener() {
			@Override
			public void onException(String message, Throwable e) {
				assertTrue(e instanceof NullPointerException);
			}
		});


		// execution
		reporter.onCounterAdded(key, new Counter());


		//assert
		assertTrue(true);
	}

	@Test
	public void testOnCounterRemoved() throws Exception {
		//data preparation
		MBeanRegistry mBeanRegistry = spy(Whitebox.newInstance(MBeanRegistry.class));
		JmxReporter reporter = Mockito.spy(new JmxReporter(mBeanRegistry));
		MetricKey key = MetricKey.newBuilder()
				.metricType(MetricKey.MetricType.counter)
				.category("Invokes")
				.name("testOnCounterRemoved")
				.build();
		//mock
		doNothing().when(mBeanRegistry).unregister(any(ObjectName.class));

		// execution
		reporter.onCounterRemoved(key);


		//assert
		assertTrue(true);
	}

	@Test
	public void testOnCounterRemoved_null() throws Exception {
		//data preparation
		MBeanRegistry mBeanRegistry = spy(Whitebox.newInstance(MBeanRegistry.class));
		JmxReporter reporter = Mockito.spy(new JmxReporter(mBeanRegistry));
		MetricKey key = MetricKey.newBuilder()
				.metricType(MetricKey.MetricType.counter)
				.category("Invokes")
				.name("testOnCounterRemoved_null")
				.build();
		//mock
		UniformSwallowHolder.setListener(new SwallowExceptionListener() {
			@Override
			public void onException(String message, Throwable e) {
				assertTrue(e instanceof NullPointerException);
			}
		});

		// execution
		reporter.onCounterRemoved(key);


		//assert
		assertTrue(true);
	}

	@Test
	public void testOnCounterRemoved_MalformedObjectNameException() throws Exception {
		//data preparation
		JmxReporter reporter = Mockito.spy(new JmxReporter());
		MetricKey key = MetricKey.newBuilder()
				.metricType(MetricKey.MetricType.counter)
				.category("Invokes")
				.name("testOnCounterRemoved_MalformedObjectNameException")
				.build();
		//mock
		doThrow(MalformedObjectNameException.class).when(reporter).createOName(key);
		UniformSwallowHolder.setListener(new SwallowExceptionListener() {
			@Override
			public void onException(String message, Throwable e) {
				assertTrue(e instanceof MalformedObjectNameException);
			}
		});


		// execution
		reporter.onCounterRemoved(key);


		//assert
		assertTrue(true);
	}

	@Test
	public void testOnCounterRemoved_Exception() throws Exception {
		//data preparation
		JmxReporter reporter = Mockito.spy(new JmxReporter());
		MetricKey key = MetricKey.newBuilder()
				.metricType(MetricKey.MetricType.counter)
				.category("Invokes")
				.name("testOnCounterRemoved_Exception")
				.build();
		//mock
		doThrow(RuntimeException.class).when(reporter).createOName(key);
		UniformSwallowHolder.setListener(new SwallowExceptionListener() {
			@Override
			public void onException(String message, Throwable e) {
				assertTrue(e instanceof RuntimeException);
			}
		});


		// execution
		reporter.onCounterRemoved(key);


		//assert
		assertTrue(true);
	}


	@Test
	public void testOnHistogramAdded() throws Exception {
		//data preparation
		MBeanRegistry mBeanRegistry = spy(Whitebox.newInstance(MBeanRegistry.class));
		JmxReporter reporter = Mockito.spy(new JmxReporter(mBeanRegistry));
		MetricKey key = MetricKey.newBuilder()
				.metricType(MetricKey.MetricType.counter)
				.category("Invokes")
				.name("testOnHistogramAdded")
				.build();
		//mock
		doNothing().when(mBeanRegistry).register(any(ObjectName.class), any(JmxHistogramMXBean.JmxHistogram.class));

		// execution
		reporter.onHistogramAdded(key, new Histogram());


		//assert
		assertTrue(true);
	}

	@Test
	public void testOnHistogramAdded_null() throws Exception {
		//data preparation
		MBeanRegistry mBeanRegistry = spy(Whitebox.newInstance(MBeanRegistry.class));
		JmxReporter reporter = Mockito.spy(new JmxReporter(mBeanRegistry));
		MetricKey key = MetricKey.newBuilder()
				.metricType(MetricKey.MetricType.histogram)
				.category("Invokes")
				.name("testOnHistogramAdded_null")
				.build();
		//mock

		// execution
		reporter.onHistogramAdded(key, null);


		//assert
		assertTrue(true);
	}

	@Test
	public void testOnHistogramAdded_MalformedObjectNameException() throws Exception {
		//data preparation
		JmxReporter reporter = Mockito.spy(new JmxReporter());
		MetricKey key = MetricKey.newBuilder()
				.metricType(MetricKey.MetricType.histogram)
				.category("Invokes")
				.name("testOnHistogramAdded_MalformedObjectNameException")
				.build();
		//mock
		doThrow(MalformedObjectNameException.class).when(reporter).createOName(key);
		UniformSwallowHolder.setListener(new SwallowExceptionListener() {
			@Override
			public void onException(String message, Throwable e) {
				assertTrue(e instanceof MalformedObjectNameException);
			}
		});


		// execution
		reporter.onHistogramAdded(key, new Histogram());


		//assert
		assertTrue(true);
	}

	@Test
	public void testOnHistogramAdded_Exception() throws Exception {
		//data preparation
		JmxReporter reporter = Mockito.spy(new JmxReporter());
		MetricKey key = MetricKey.newBuilder()
				.metricType(MetricKey.MetricType.histogram)
				.category("Invokes")
				.name("testOnHistogramAdded_Exception")
				.build();
		//mock
		doThrow(NullPointerException.class).when(reporter).createOName(key);
		UniformSwallowHolder.setListener(new SwallowExceptionListener() {
			@Override
			public void onException(String message, Throwable e) {
				assertTrue(e instanceof NullPointerException);
			}
		});


		// execution
		reporter.onHistogramAdded(key, new Histogram());


		//assert
		assertTrue(true);
	}

	@Test
	public void testOnHistogramRemoved() throws Exception {
		//data preparation
		MBeanRegistry mBeanRegistry = spy(Whitebox.newInstance(MBeanRegistry.class));
		JmxReporter reporter = Mockito.spy(new JmxReporter(mBeanRegistry));
		MetricKey key = MetricKey.newBuilder()
				.metricType(MetricKey.MetricType.histogram)
				.category("Invokes")
				.name("testOnHistogramRemoved")
				.build();
		//mock
		doNothing().when(mBeanRegistry).unregister(any(ObjectName.class));

		// execution
		reporter.onHistogramRemoved(key);


		//assert
		assertTrue(true);
	}

	@Test
	public void testOnHistogramRemoved_null() throws Exception {
		//data preparation
		MBeanRegistry mBeanRegistry = spy(Whitebox.newInstance(MBeanRegistry.class));
		JmxReporter reporter = Mockito.spy(new JmxReporter(mBeanRegistry));
		MetricKey key = MetricKey.newBuilder()
				.metricType(MetricKey.MetricType.histogram)
				.category("Invokes")
				.name("testOnHistogramRemoved_null")
				.build();
		//mock
		UniformSwallowHolder.setListener(new SwallowExceptionListener() {
			@Override
			public void onException(String message, Throwable e) {
				assertTrue(e instanceof NullPointerException);
			}
		});

		// execution
		reporter.onHistogramRemoved(key);


		//assert
		assertTrue(true);
	}

	@Test
	public void testOnHistogramRemoved_MalformedObjectNameException() throws Exception {
		//data preparation
		JmxReporter reporter = Mockito.spy(new JmxReporter());
		MetricKey key = MetricKey.newBuilder()
				.metricType(MetricKey.MetricType.histogram)
				.category("Invokes")
				.name("testOnHistogramRemoved_null")
				.build();
		//mock
		doThrow(MalformedObjectNameException.class).when(reporter).createOName(key);
		UniformSwallowHolder.setListener(new SwallowExceptionListener() {
			@Override
			public void onException(String message, Throwable e) {
				assertTrue(e instanceof MalformedObjectNameException);
			}
		});


		// execution
		reporter.onHistogramRemoved(key);


		//assert
		assertTrue(true);
	}

	@Test
	public void testOnHistogramRemoved_Exception() throws Exception {
		//data preparation
		JmxReporter reporter = Mockito.spy(new JmxReporter());
		MetricKey key = MetricKey.newBuilder()
				.metricType(MetricKey.MetricType.histogram)
				.category("Invokes")
				.name("testOnHistogramRemoved_Exception")
				.build();
		//mock
		doThrow(RuntimeException.class).when(reporter).createOName(key);
		UniformSwallowHolder.setListener(new SwallowExceptionListener() {
			@Override
			public void onException(String message, Throwable e) {
				assertTrue(e instanceof RuntimeException);
			}
		});


		// execution
		reporter.onHistogramRemoved(key);


		//assert
		assertTrue(true);
	}

	@Test
	public void testMetricRegistryListenerBase() throws Exception {
		MetricRegistryListener reporter = new MetricRegistryListener() {

		};

		MetricKey key = MetricKey.newBuilder()
				.metricType(MetricKey.MetricType.histogram)
				.category("Invokes")
				.name("testMetricRegistryListenerBase")
				.build();
		reporter.onCounterAdded(key, new Counter());
		reporter.onCounterRemoved(key);

		reporter.onHistogramAdded(key, new Histogram());
		reporter.onHistogramRemoved(key);

		assertTrue(true);
	}

	@Test
	public void testClose() throws IOException {
		JmxReporter reporter = Mockito.spy(new JmxReporter());
		reporter.close();
		assertTrue(true);
	}


}
