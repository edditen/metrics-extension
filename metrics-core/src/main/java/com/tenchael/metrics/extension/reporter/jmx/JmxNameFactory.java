package com.tenchael.metrics.extension.reporter.jmx;

import com.tenchael.jmx.ext.Commons;
import com.tenchael.metrics.extension.metrics.MetricKey;
import com.tenchael.metrics.extension.metrics.NameFactory;

/**
 * JmxNameFactory with JMX convention that is:<br>
 * specificDomain:type=specificType,name="specificName"<br>
 * Created by Tenchael on 2020/2/19.
 */
public class JmxNameFactory implements NameFactory {
	public static final String DEFAULT_DOMAIN_PREFIX = "metrics.";
	private final String domainPrefix;


	public JmxNameFactory(String domainPrefix) {
		this.domainPrefix = domainPrefix;
	}

	public JmxNameFactory() {
		this(DEFAULT_DOMAIN_PREFIX);
	}

	@Override
	public String createName(MetricKey key) {
		return Commons.oname(domain(key.getMetricType()), key.getCategory(), key.getName());
	}

	public String domain(MetricKey.MetricType type) {
		return new StringBuilder(domainPrefix)
				.append(type)
				.toString();
	}


}
