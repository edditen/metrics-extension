package com.tenchael.metrics.extension.metrics;

import org.junit.Assert;
import org.junit.Test;

public class CounterTests extends Assert {

	@Test
	public void testIncr() {
		Counter counter = new Counter();
		counter.incr();
		assertEquals(1, counter.getCount());
	}

	@Test
	public void testDecr() {
		Counter counter = new Counter(10);
		counter.decr();
		assertEquals(9, counter.getCount());
	}

	@Test
	public void testIncrDelta() {
		Counter counter = new Counter();
		counter.incr(5);
		assertEquals(5, counter.getCount());
	}

	@Test
	public void testDecrDelta() {
		Counter counter = new Counter(10);
		counter.decr(4);
		assertEquals(6, counter.getCount());
	}

}
