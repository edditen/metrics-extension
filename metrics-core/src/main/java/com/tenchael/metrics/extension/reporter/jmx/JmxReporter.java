package com.tenchael.metrics.extension.reporter.jmx;

import com.tenchael.metrics.extension.metrics.Counter;
import com.tenchael.metrics.extension.metrics.Histogram;
import com.tenchael.metrics.extension.metrics.MetricRegistryListener;

import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

import static com.tenchael.metrics.extension.utils.SwallowExceptionHandler.swallow;

/**
 * JMX reporter
 * Created by Tenchael on 2019/11/26.
 */
public class JmxReporter {

	private JmxReporter() throws IllegalAccessException {
		throw new IllegalAccessException("illegal access");
	}

	/**
	 * JMX bean register or unregister notification
	 */
	public static class JmxListener extends MetricRegistryListener.Base {

		private static final JmxListener INSTANCE = new JmxListener();

		private final MBeanRegistry registry;

		public JmxListener() {
			this(MBeanRegistry.getInstance());
		}

		public JmxListener(MBeanRegistry registry) {
			this.registry = registry;
		}

		public static JmxListener getInstance() {
			return INSTANCE;
		}


		public ObjectName createName(String name) throws MalformedObjectNameException {
			return new ObjectName(name);
		}


		private void onMetricsRemoved(String name) {
			try {
				ObjectName oname = createName(name);
				registry.unregister(oname);
			} catch (MalformedObjectNameException e) {
				swallow(e);
			} catch (Exception e) {
				swallow(e);
			}
		}

		@Override
		public void onCounterAdded(String name, Counter counter) {
			if (counter == null) {
				return;
			}
			try {
				ObjectName oname = createName(name);
				registry.register(oname, new JmxCounterMXBean.JmxCounter(oname, counter));
			} catch (MalformedObjectNameException e) {
				swallow(e);
			} catch (Exception e) {
				swallow(e);
			}
		}

		@Override
		public void onCounterRemoved(String name) {
			onMetricsRemoved(name);
		}

		@Override
		public void onHistogramAdded(String name, Histogram histogram) {
			if (histogram == null) {
				return;
			}
			try {
				ObjectName oname = createName(name);
				registry.register(oname, new JmxHistogramMXBean.JmxHistogram(oname, histogram));
			} catch (MalformedObjectNameException e) {
				swallow(e);
			} catch (Exception e) {
				swallow(e);
			}
		}

		@Override
		public void onHistogramRemoved(String name) {
			onMetricsRemoved(name);
		}

	}
}
