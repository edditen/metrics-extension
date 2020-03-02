package com.tenchael.metrics.extension.reporter.jmx;

import com.tenchael.jmx.ext.MBeanRegistry;
import com.tenchael.metrics.extension.common.logger.Logger;
import com.tenchael.metrics.extension.common.logger.LoggerFactory;
import com.tenchael.metrics.extension.metrics.Counter;
import com.tenchael.metrics.extension.metrics.Histogram;
import com.tenchael.metrics.extension.metrics.MetricKey;
import com.tenchael.metrics.extension.metrics.NameFactory;
import com.tenchael.metrics.extension.reporter.Reporter;

import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

/**
 * JMX reporter
 * Created by Tenchael on 2019/11/26.
 */
public class JmxReporter implements Reporter {

	private static final Logger LOGGER = LoggerFactory.getLogger(JmxReporter.class);

	private final NameFactory nameFactory;
	private final MBeanRegistry mBeanRegistry;

	public JmxReporter() {
		this(MBeanRegistry.getInstance());
	}

	public JmxReporter(MBeanRegistry mBeanRegistry) {
		this.mBeanRegistry = mBeanRegistry;
		this.nameFactory = new JmxNameFactory();
	}

	public ObjectName createOName(MetricKey key) throws MalformedObjectNameException {
		return new ObjectName(nameFactory.createName(key));
	}

	private void onMetricsRemoved(MetricKey key) {
		try {
			ObjectName oname = createOName(key);
			mBeanRegistry.unregister(oname);
		} catch (MalformedObjectNameException e) {
			LOGGER.error(e);
		} catch (Exception e) {
			LOGGER.error(e);
		}
	}

	@Override
	public void onCounterAdded(MetricKey key, Counter counter) {
		if (counter == null) {
			return;
		}
		try {
			ObjectName oname = createOName(key);
			mBeanRegistry.register(oname, new JmxCounterMXBean.JmxCounter(oname, counter));
		} catch (MalformedObjectNameException e) {
			LOGGER.error(e);
		} catch (Exception e) {
			LOGGER.error(e);
		}
	}

	@Override
	public void onCounterRemoved(MetricKey key) {
		onMetricsRemoved(key);
	}

	@Override
	public void onHistogramAdded(MetricKey key, Histogram histogram) {
		if (histogram == null) {
			return;
		}
		try {
			ObjectName oname = createOName(key);
			mBeanRegistry.register(oname, new JmxHistogramMXBean.JmxHistogram(oname, histogram));
		} catch (MalformedObjectNameException e) {
			LOGGER.error(e);
		} catch (Exception e) {
			LOGGER.error(e);
		}
	}

	@Override
	public void onHistogramRemoved(MetricKey key) {
		onMetricsRemoved(key);
	}

	@Override
	public void close() {
		this.mBeanRegistry.unregisterAll();
	}
}