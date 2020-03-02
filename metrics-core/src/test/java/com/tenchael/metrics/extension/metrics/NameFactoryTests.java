package com.tenchael.metrics.extension.metrics;

import org.junit.Assert;
import org.junit.Test;

/**
 * Created by Tenchael on 2020/2/19.
 */
public class NameFactoryTests extends Assert {


	@Test
	public void testCreateName() {
		NameFactory.DefaultNameFactory factory = new NameFactory.DefaultNameFactory();
		MetricKey key = MetricKey.newBuilder()
				.metricType(MetricKey.MetricType.counter)
				.category("Test")
				.name("testCreateName")
				.build();
		String name = factory.createName(key);
		System.out.println(name);
		assertEquals("counter:Test:testCreateName", name);
	}
}
