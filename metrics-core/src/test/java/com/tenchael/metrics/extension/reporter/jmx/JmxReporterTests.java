package com.tenchael.metrics.extension.reporter.jmx;

import com.tenchael.metrics.extension.metrics.Counter;
import com.tenchael.metrics.extension.metrics.Histogram;
import com.tenchael.metrics.extension.metrics.MetricRegistryListener;
import com.tenchael.metrics.extension.support.Whitebox;
import com.tenchael.metrics.extension.utils.SwallowExceptionListener;
import com.tenchael.metrics.extension.utils.UniformSwallowHolder;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

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
		try {
			Whitebox.newInstance(JmxReporter.class);
			assertFalse(true);
		} catch (Exception e) {
			e.printStackTrace();
			assertTrue(e.getCause() instanceof IllegalAccessException);
		}
	}

	@Test
	public void testJmxListener() {
		JmxReporter.JmxListener listener = new JmxReporter.JmxListener();
		Object registry = Whitebox.getInternalState(listener, "registry");
		assertEquals(MBeanRegistry.getInstance(), registry);
	}

	@Test
	public void testCreateName() throws Exception {
		MBeanRegistry mBeanRegistry = spy(Whitebox.newInstance(MBeanRegistry.class));
		JmxReporter.JmxListener listener = spy(new JmxReporter.JmxListener(mBeanRegistry));
		String name = "com.tenchael.metrics.extension.reporter.jmx:type=Ruok,name=\"testCreateOname\"";
		ObjectName oname = listener.createName(name);
		assertNotNull(oname);
	}

	@Test
	public void testOnCounterAdded() throws Exception {
		//data preparation
		MBeanRegistry mBeanRegistry = spy(Whitebox.newInstance(MBeanRegistry.class));
		JmxReporter.JmxListener listener = spy(new JmxReporter.JmxListener(mBeanRegistry));
		String name = "com.tenchael.metrics.extension.reporter.jmx:type=Test,name=\"testOnCounterAdded\"";

		//mock
		doNothing().when(mBeanRegistry).register(any(ObjectName.class), any(JmxCounterMXBean.JmxCounter.JmxCounter.class));

		// execution
		listener.onCounterAdded(name, new Counter());


		//assert
		assertTrue(true);
	}

	@Test
	public void testOnCounterAdded_null() throws Exception {
		//data preparation
		MBeanRegistry mBeanRegistry = spy(Whitebox.newInstance(MBeanRegistry.class));
		JmxReporter.JmxListener listener = spy(new JmxReporter.JmxListener(mBeanRegistry));
		String name = "com.tenchael.metrics.extension.reporter.jmx:type=Test,name=\"testOnCounterAdded_null\"";

		//mock

		// execution
		listener.onCounterAdded(name, null);


		//assert
		assertTrue(true);
	}

	@Test
	public void testOnCounterAdded_MalformedObjectNameException() throws Exception {
		//data preparation
		JmxReporter.JmxListener listener = spy(new JmxReporter.JmxListener());
		String name = "com.tenchael.metrics.extension.reporter.jmx:type=Test,name=\"testOnCounterAdded_MalformedObjectNameException\"";

		//mock
		doThrow(MalformedObjectNameException.class).when(listener).createName(name);
		UniformSwallowHolder.setListener(new SwallowExceptionListener() {
			@Override
			public void onException(String message, Exception e) {
				assertTrue(e instanceof MalformedObjectNameException);
			}
		});


		// execution
		listener.onCounterAdded(name, new Counter());


		//assert
		assertTrue(true);
	}

	@Test
	public void testOnCounterAdded_Exception() throws Exception {
		//data preparation
		JmxReporter.JmxListener listener = spy(new JmxReporter.JmxListener());
		String name = "com.tenchael.metrics.extension.reporter.jmx:type=Test,name=\"testOnCounterAdded_Exception\"";

		//mock
		doThrow(NullPointerException.class).when(listener).createName(name);
		UniformSwallowHolder.setListener(new SwallowExceptionListener() {
			@Override
			public void onException(String message, Exception e) {
				assertTrue(e instanceof NullPointerException);
			}
		});


		// execution
		listener.onCounterAdded(name, new Counter());


		//assert
		assertTrue(true);
	}

	@Test
	public void testOnCounterRemoved() throws Exception {
		//data preparation
		MBeanRegistry mBeanRegistry = spy(Whitebox.newInstance(MBeanRegistry.class));
		JmxReporter.JmxListener listener = spy(new JmxReporter.JmxListener(mBeanRegistry));
		String name = "com.tenchael.metrics.extension.reporter.jmx:type=Test,name=\"testOnCounterRemoved\"";

		//mock
		doNothing().when(mBeanRegistry).unregister(any(ObjectName.class));

		// execution
		listener.onCounterRemoved(name);


		//assert
		assertTrue(true);
	}

	@Test
	public void testOnCounterRemoved_null() throws Exception {
		//data preparation
		MBeanRegistry mBeanRegistry = spy(Whitebox.newInstance(MBeanRegistry.class));
		JmxReporter.JmxListener listener = spy(new JmxReporter.JmxListener(mBeanRegistry));
		String name = "com.tenchael.metrics.extension.reporter.jmx:type=Test,name=\"testOnCounterRemoved_null\"";

		//mock
		UniformSwallowHolder.setListener(new SwallowExceptionListener() {
			@Override
			public void onException(String message, Exception e) {
				assertTrue(e instanceof NullPointerException);
			}
		});

		// execution
		listener.onCounterRemoved(name);


		//assert
		assertTrue(true);
	}

	@Test
	public void testOnCounterRemoved_MalformedObjectNameException() throws Exception {
		//data preparation
		JmxReporter.JmxListener listener = spy(new JmxReporter.JmxListener());
		String name = "com.tenchael.metrics.extension.reporter.jmx:type=Test,name=\"testOnCounterRemoved_MalformedObjectNameException\"";

		//mock
		doThrow(MalformedObjectNameException.class).when(listener).createName(name);
		UniformSwallowHolder.setListener(new SwallowExceptionListener() {
			@Override
			public void onException(String message, Exception e) {
				assertTrue(e instanceof MalformedObjectNameException);
			}
		});


		// execution
		listener.onCounterRemoved(name);


		//assert
		assertTrue(true);
	}

	@Test
	public void testOnCounterRemoved_Exception() throws Exception {
		//data preparation
		JmxReporter.JmxListener listener = spy(new JmxReporter.JmxListener());
		String name = "com.tenchael.metrics.extension.reporter.jmx:type=Test,name=\"testOnCounterRemoved_MalformedObjectNameException\"";

		//mock
		doThrow(RuntimeException.class).when(listener).createName(name);
		UniformSwallowHolder.setListener(new SwallowExceptionListener() {
			@Override
			public void onException(String message, Exception e) {
				assertTrue(e instanceof RuntimeException);
			}
		});


		// execution
		listener.onCounterRemoved(name);


		//assert
		assertTrue(true);
	}


	@Test
	public void testOnHistogramAdded() throws Exception {
		//data preparation
		MBeanRegistry mBeanRegistry = spy(Whitebox.newInstance(MBeanRegistry.class));
		JmxReporter.JmxListener listener = spy(new JmxReporter.JmxListener(mBeanRegistry));
		String name = "com.tenchael.metrics.extension.reporter.jmx:type=Test,name=\"testOnHistogramAdded\"";

		//mock
		doNothing().when(mBeanRegistry).register(any(ObjectName.class), any(JmxHistogramMXBean.JmxHistogram.class));

		// execution
		listener.onHistogramAdded(name, new Histogram());


		//assert
		assertTrue(true);
	}

	@Test
	public void testOnHistogramAdded_null() throws Exception {
		//data preparation
		MBeanRegistry mBeanRegistry = spy(Whitebox.newInstance(MBeanRegistry.class));
		JmxReporter.JmxListener listener = spy(new JmxReporter.JmxListener(mBeanRegistry));
		String name = "com.tenchael.metrics.extension.reporter.jmx:type=Test,name=\"testOnHistogramAdded_null\"";

		//mock

		// execution
		listener.onHistogramAdded(name, null);


		//assert
		assertTrue(true);
	}

	@Test
	public void testOnHistogramAdded_MalformedObjectNameException() throws Exception {
		//data preparation
		JmxReporter.JmxListener listener = spy(new JmxReporter.JmxListener());
		String name = "com.tenchael.metrics.extension.reporter.jmx:type=Test,name=\"testOnHistogramAdded_MalformedObjectNameException\"";

		//mock
		doThrow(MalformedObjectNameException.class).when(listener).createName(name);
		UniformSwallowHolder.setListener(new SwallowExceptionListener() {
			@Override
			public void onException(String message, Exception e) {
				assertTrue(e instanceof MalformedObjectNameException);
			}
		});


		// execution
		listener.onHistogramAdded(name, new Histogram());


		//assert
		assertTrue(true);
	}

	@Test
	public void testOnHistogramAdded_Exception() throws Exception {
		//data preparation
		JmxReporter.JmxListener listener = spy(new JmxReporter.JmxListener());
		String name = "com.tenchael.metrics.extension.reporter.jmx:type=Test,name=\"testOnHistogramAdded_Exception\"";

		//mock
		doThrow(NullPointerException.class).when(listener).createName(name);
		UniformSwallowHolder.setListener(new SwallowExceptionListener() {
			@Override
			public void onException(String message, Exception e) {
				assertTrue(e instanceof NullPointerException);
			}
		});


		// execution
		listener.onHistogramAdded(name, new Histogram());


		//assert
		assertTrue(true);
	}

	@Test
	public void testOnHistogramRemoved() throws Exception {
		//data preparation
		MBeanRegistry mBeanRegistry = spy(Whitebox.newInstance(MBeanRegistry.class));
		JmxReporter.JmxListener listener = spy(new JmxReporter.JmxListener(mBeanRegistry));
		String name = "com.tenchael.metrics.extension.reporter.jmx:type=Test,name=\"testOnHistogramRemoved\"";

		//mock
		doNothing().when(mBeanRegistry).unregister(any(ObjectName.class));

		// execution
		listener.onHistogramRemoved(name);


		//assert
		assertTrue(true);
	}

	@Test
	public void testOnHistogramRemoved_null() throws Exception {
		//data preparation
		MBeanRegistry mBeanRegistry = spy(Whitebox.newInstance(MBeanRegistry.class));
		JmxReporter.JmxListener listener = spy(new JmxReporter.JmxListener(mBeanRegistry));
		String name = "com.tenchael.metrics.extension.reporter.jmx:type=Test,name=\"testOnHistogramRemoved_null\"";

		//mock
		UniformSwallowHolder.setListener(new SwallowExceptionListener() {
			@Override
			public void onException(String message, Exception e) {
				assertTrue(e instanceof NullPointerException);
			}
		});

		// execution
		listener.onHistogramRemoved(name);


		//assert
		assertTrue(true);
	}

	@Test
	public void testOnHistogramRemoved_MalformedObjectNameException() throws Exception {
		//data preparation
		JmxReporter.JmxListener listener = spy(new JmxReporter.JmxListener());
		String name = "com.tenchael.metrics.extension.reporter.jmx:type=Test,name=\"testOnHistogramRemoved_MalformedObjectNameException\"";

		//mock
		doThrow(MalformedObjectNameException.class).when(listener).createName(name);
		UniformSwallowHolder.setListener(new SwallowExceptionListener() {
			@Override
			public void onException(String message, Exception e) {
				assertTrue(e instanceof MalformedObjectNameException);
			}
		});


		// execution
		listener.onHistogramRemoved(name);


		//assert
		assertTrue(true);
	}

	@Test
	public void testOnHistogramRemoved_Exception() throws Exception {
		//data preparation
		JmxReporter.JmxListener listener = spy(new JmxReporter.JmxListener());
		String name = "com.tenchael.metrics.extension.reporter.jmx:type=Test,name=\"testOnHistogramRemoved_MalformedObjectNameException\"";

		//mock
		doThrow(RuntimeException.class).when(listener).createName(name);
		UniformSwallowHolder.setListener(new SwallowExceptionListener() {
			@Override
			public void onException(String message, Exception e) {
				assertTrue(e instanceof RuntimeException);
			}
		});


		// execution
		listener.onHistogramRemoved(name);


		//assert
		assertTrue(true);
	}

	@Test
	public void testMetricRegistryListenerBase() throws Exception {
		MetricRegistryListener listener = new MetricRegistryListener.Base() {
			@Override
			public void onCounterAdded(String name, Counter counter) {
				super.onCounterAdded(name, counter);
			}

			@Override
			public void onCounterRemoved(String name) {
				super.onCounterRemoved(name);
			}

			@Override
			public void onHistogramAdded(String name, Histogram histogram) {
				super.onHistogramAdded(name, histogram);
			}

			@Override
			public void onHistogramRemoved(String name) {
				super.onHistogramRemoved(name);
			}
		};

		String name = "someName";
		listener.onCounterAdded(name, new Counter());
		listener.onCounterRemoved(name);

		listener.onHistogramAdded(name, new Histogram());
		listener.onHistogramRemoved(name);

		assertTrue(true);
	}


}
