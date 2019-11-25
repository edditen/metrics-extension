package com.tenchael.metrics.extension.reporter.jmx;

import com.tenchael.metrics.extension.metrics.Counter;
import com.tenchael.metrics.extension.metrics.Histogram;
import com.tenchael.metrics.extension.metrics.MetricRegistryListener;
import com.tenchael.metrics.extension.utils.SwallowExceptionHandler;

import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

public class JmxReporter {

	public JmxReporter() {
	}

	public static class JmxListener extends MetricRegistryListener.Base {

		private volatile MBeanRegistry registry = MBeanRegistry.getInstance();

		private ObjectName createName(String name) throws MalformedObjectNameException {
			return new ObjectName(name);
		}


		private void onMetricsRemoved(String name) {
			try {
				ObjectName oname = createName(name);
				registry.unregister(oname);
			} catch (MalformedObjectNameException e) {
				SwallowExceptionHandler.swallow(e);
			} catch (Exception e) {
				SwallowExceptionHandler.swallow(e);
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
				SwallowExceptionHandler.swallow(e);
			} catch (Exception e) {
				SwallowExceptionHandler.swallow(e);
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
				SwallowExceptionHandler.swallow(e);
			} catch (Exception e) {
				SwallowExceptionHandler.swallow(e);
			}
		}

		@Override
		public void onHistogramRemoved(String name) {
			onMetricsRemoved(name);
		}

	}
}
