package com.tenchael.metrics.extension.protocol;

import com.tenchael.jmx.ext.JmxConfig;
import com.tenchael.jmx.ext.JmxConfigListener;
import com.tenchael.metrics.extension.common.PropertiesManager;
import com.tenchael.metrics.extension.common.logger.Logger;
import com.tenchael.metrics.extension.common.logger.LoggerFactory;
import com.tenchael.metrics.extension.metrics.Histogram;
import com.tenchael.metrics.extension.metrics.MetricKey;
import com.tenchael.metrics.extension.metrics.MetricsRegistry;

import static com.tenchael.metrics.extension.common.Constants.PropsKey.METRICS_ENABLE;

public interface Protocol {

	boolean metricsEnable();

	class Base implements Protocol {

		private static final Logger LOGGER = LoggerFactory.getLogger(Base.class);

		protected final MetricsRegistry registery;
		private volatile boolean metricsEnable;

		public Base() {
			this.registery = MetricsRegistry.getInstance();
			this.metricsEnable = PropertiesManager.getInstance().
					getBoolOrDefault(METRICS_ENABLE, true);
			JmxConfig jmxConfig = JmxConfig.instance();
			jmxConfig.setEnabled(metricsEnable);
			jmxConfig.addListener(new JmxConfigListener() {
				@Override
				public void onUpdate(boolean enabled) {
					metricsEnable = enabled;
					LOGGER.info(String.format("jmx metrics enable value: %s", enabled));
					if (enabled == false) {
						LOGGER.warn(String.format("unregister all metrics"));
						registery.unregisterAll();
					}
				}
			});
		}

		@Override
		public boolean metricsEnable() {
			return this.metricsEnable;
		}

		protected void counterIncr(String type, String name) {
			try {
				MetricKey key = MetricKey.newBuilder()
						.metricType(MetricKey.MetricType.counter)
						.category(type)
						.name(name)
						.build();
				registery.counter(key).incr();
			} catch (Exception e) {
				LOGGER.warn("counter occurs error", e);
			}
		}

		protected Histogram.Context beginRecord(String type, String name) {
			try {
				MetricKey key = MetricKey.newBuilder()
						.metricType(MetricKey.MetricType.histogram)
						.category(type)
						.name(name)
						.build();
				return registery.histogram(key).time();
			} catch (Exception e) {
				LOGGER.warn("histogram occurs error", e);
				return null;
			}
		}
	}

}
