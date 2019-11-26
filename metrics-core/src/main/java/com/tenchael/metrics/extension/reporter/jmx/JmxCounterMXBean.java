package com.tenchael.metrics.extension.reporter.jmx;

import com.tenchael.metrics.extension.metrics.Counter;

import javax.management.ObjectName;

/**
 * JMX bean for counter
 * Created by Tenchael on 2019/11/26.
 */
public interface JmxCounterMXBean extends MBean {

	long getCount();

	class JmxCounter extends BaseMBean implements JmxCounterMXBean {

		private final Counter metrics;

		public JmxCounter(ObjectName oname, Counter metrics) {
			super(oname);
			this.metrics = metrics;
		}

		@Override
		public long getCount() {
			return metrics.getCount();
		}

	}


}
