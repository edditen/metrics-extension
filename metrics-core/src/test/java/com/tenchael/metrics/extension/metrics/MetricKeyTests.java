package com.tenchael.metrics.extension.metrics;

import org.junit.Assert;
import org.junit.Test;

/**
 * Created by Tenchael on 2020/2/20.
 */
public class MetricKeyTests extends Assert {

	@Test
	public void testToString() {
		MetricKey key = MetricKey.newBuilder()
				.metricType(MetricKey.MetricType.counter)
				.category("Test")
				.name("testToString")
				.build();
		System.out.println(key);
		assertEquals("counter:Test:testToString", key.toString());
	}

	@Test
	public void testToString_withNull() {
		MetricKey key = MetricKey.newBuilder()
//				.metricType(MetricKey.MetricType.counter)
				.category("Test")
				.name("testToString_withNull")
				.build();
		System.out.println(key);
		assertEquals("null:Test:testToString_withNull", key.toString());
	}

	@Test
	public void testHashcode() {
		MetricKey key = MetricKey.newBuilder()
				.metricType(MetricKey.MetricType.counter)
				.category("Test")
				.name("testHashcode")
				.build();
		System.out.println(key.hashCode());
		int expect = MetricKey.MetricType.counter.hashCode() * 97
				+ "Test".hashCode() * 31
				+ "testHashcode".hashCode();
		assertEquals(expect, key.hashCode());
		System.out.println(Integer.MAX_VALUE / 2 + Integer.MAX_VALUE * 3);
	}

	@Test
	public void testEquals() {
		MetricKey key1 = MetricKey.newBuilder()
				.metricType(MetricKey.MetricType.counter)
				.category("Test")
				.name("testEquals")
				.build();

		MetricKey key2 = MetricKey.newBuilder()
				.metricType(MetricKey.MetricType.counter)
				.category("Test")
				.name("testEquals")
				.build();
		assertTrue(key1 == key1);
		assertTrue(key1 != key2);
		assertTrue(key1.hashCode() == key2.hashCode());
		assertTrue(key1.equals(key1));
		assertTrue(key1.equals(key2));
		assertTrue(!key1.equals(null));
		assertTrue(!key1.equals("hello"));
	}

	@Test
	public void testEquals_metricType() {
		MetricKey key = new MetricKey();
		MetricKey.MetricType t1 = null;
		MetricKey.MetricType t2 = null;
		assertTrue(key.equals(t1, t2));

		t1 = MetricKey.MetricType.counter;
		t2 = null;
		assertTrue(!key.equals(t1, t2));

		t1 = MetricKey.MetricType.counter;
		t2 = MetricKey.MetricType.histogram;
		assertTrue(!key.equals(t1, t2));

		t1 = MetricKey.MetricType.counter;
		t2 = MetricKey.MetricType.counter;
		assertTrue(key.equals(t1, t2));
	}

	@Test
	public void testEquals_string() {
		MetricKey key = new MetricKey();

		String s1 = null;
		String s2 = null;
		assertTrue(key.equals(s1, s2));

		s1 = "hello";
		s2 = null;
		assertTrue(!key.equals(s1, s2));

		s1 = "hello";
		s2 = "world";
		assertTrue(!key.equals(s1, s2));

		s1 = "hello";
		s2 = "hello";
		assertTrue(key.equals(s1, s2));
	}

	@Test
	public void testGetSet() {
		MetricKey key = new MetricKey();
		key.setMetricType(MetricKey.MetricType.counter);
		key.setCategory("Test");
		key.setName("testGetSet");
		assertEquals(MetricKey.MetricType.counter, key.getMetricType());
		assertEquals("Test", key.getCategory());
		assertEquals("testGetSet", key.getName());

	}


}
