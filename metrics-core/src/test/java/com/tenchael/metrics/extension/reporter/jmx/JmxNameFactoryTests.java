package com.tenchael.metrics.extension.reporter.jmx;

import com.tenchael.metrics.extension.metrics.MetricKey;
import com.tenchael.metrics.extension.metrics.NameFactory;
import com.tenchael.metrics.extension.support.Whitebox;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by Tenchael on 2020/2/19.
 */
public class JmxNameFactoryTests extends Assert {


	@Test
	public void testConstruct() {
		NameFactory nameFactory = new JmxNameFactory();
		Object domainPrefix = Whitebox.getInternalState(nameFactory, "domainPrefix");
		assertEquals(JmxNameFactory.DEFAULT_DOMAIN_PREFIX, domainPrefix);
	}

	@Test
	public void testConstruct_withDomain() {
		NameFactory nameFactory = new JmxNameFactory("hello.");
		Object domainPrefix = Whitebox.getInternalState(nameFactory, "domainPrefix");
		assertEquals("hello.", domainPrefix);
	}


	@Test
	public void testDomain_histogram() {
		String domain = new JmxNameFactory().domain(MetricKey.MetricType.histogram);
		System.out.println(domain);
		String expect = JmxNameFactory.DEFAULT_DOMAIN_PREFIX + MetricKey.MetricType.histogram.name();
		assertEquals(expect, domain);
	}

	@Test
	public void testDomain_counter() {
		String domain = new JmxNameFactory().domain(MetricKey.MetricType.counter);
		System.out.println(domain);
		String expect = JmxNameFactory.DEFAULT_DOMAIN_PREFIX + MetricKey.MetricType.counter.name();
		assertEquals(expect, domain);
	}


	@Test
	public void testCreateName() {
		MetricKey key = MetricKey.newBuilder()
				.metricType(MetricKey.MetricType.histogram)
				.category("Elapse")
				.name("testCreateName")
				.build();
		String name = new JmxNameFactory("helloo.").createName(key);
		System.out.println(name);
		String expect = "helloo.histogram:type=Elapse,name=\"testCreateName\"";
		assertEquals(expect, name);
	}
}
