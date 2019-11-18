package com.tenchael.metrics.extension.reporter.jmx;

import com.tenchael.metrics.extension.metrics.Gauge;

import javax.management.ObjectName;

public interface JmxGaugeMXBean extends MBean {

    Object getValue();

    class JmxGauge extends BaseMBean implements JmxGaugeMXBean {

        private final Gauge metric;

        public JmxGauge(ObjectName oname, Gauge metric) {
            super(oname);
            this.metric = metric;
        }

        @Override
        public Object getValue() {
            return this.metric.getValue();
        }

    }
}
