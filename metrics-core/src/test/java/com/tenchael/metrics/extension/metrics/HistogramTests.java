package com.tenchael.metrics.extension.metrics;

import org.junit.Assert;
import org.junit.Test;

import java.util.Random;
import java.util.concurrent.TimeUnit;

public class HistogramTests extends Assert {

	private static final Random RANDOM = new Random();

	private static final double PRECISE = 10.0d;

	private boolean equals(double d1, double d2) {
		return equals(d1, d2, 1E-1);
	}

	private boolean equals(double d1, double d2, double precise) {
		if (d1 - d2 < precise) {
			return true;
		}
		return false;
	}

	@Test
	public void testPercentile() {
		Histogram histogram = new Histogram();
		long[] values = new long[1000];
		for (int i = 0; i < values.length; i++) {
			values[i] = (i + 1);
		}

		for (long v : values) {
			histogram.update(v);
		}

		assertEquals(1000, histogram.getCount());
		Snapshot snapshot = histogram.getSnapshot();
		assertTrue(equals(750.0d, snapshot.get75thPercentile()));
		assertTrue(equals(950.0d, snapshot.get95thPercentile()));
		assertTrue(equals(990.0d, snapshot.get99thPercentile()));
		assertTrue(equals(999.0d, snapshot.get999thPercentile()));
		assertTrue(equals(1000.0d, snapshot.getMax()));
		assertTrue(equals(1.0d, snapshot.getMin()));
		assertTrue(equals(500.0d, snapshot.getMean()));
		assertTrue(equals(500.0d, snapshot.getMedian()));
		assertTrue(equals(288.0d, snapshot.getStdDev()));
		assertArrayEquals(values, snapshot.getValues());

	}

	@Test
	public void testCount() {
		Histogram histogram = new Histogram();
		int[] values = new int[1000];
		for (int i = 0; i < values.length; i++) {
			values[i] = (i + 1);
		}

		for (int v : values) {
			histogram.update(TimeUnit.MILLISECONDS.toNanos(v));
		}

		assertEquals(1000, histogram.getCount());
		assertEquals(990, histogram.getU10());
		assertEquals(950, histogram.getU50());
		assertEquals(500, histogram.getU500());

	}


	@Test
	public void testPercentile_random() {
		Histogram histogram = new Histogram(new UniformReservoir(10000));
		for (int i = 0; i < 1000000; i++) {
			histogram.update(1 + RANDOM.nextInt(1000));
		}

		assertEquals(1000000, histogram.getCount());
		Snapshot snapshot = histogram.getSnapshot();
		assertTrue(equals(750.0d, snapshot.get75thPercentile(), PRECISE));
		assertTrue(equals(950.0d, snapshot.get95thPercentile(), PRECISE));
		assertTrue(equals(990.0d, snapshot.get99thPercentile(), PRECISE));
		assertTrue(equals(999.0d, snapshot.get999thPercentile(), PRECISE));
		assertTrue(equals(1000.0d, snapshot.getMax(), PRECISE));
		assertTrue(equals(1.0d, snapshot.getMin(), PRECISE));
		assertTrue(equals(500.0d, snapshot.getMean(), PRECISE));
		assertTrue(equals(500.0d, snapshot.getMedian(), PRECISE));
		assertTrue(equals(288.0d, snapshot.getStdDev(), PRECISE));
	}

	@Test
	public void testCount_random() {
		Histogram histogram = new Histogram();
		int count = 100000;
		for (int i = 0; i < count; i++) {
			histogram.update(1 + RANDOM.nextInt(1000));
		}

		assertEquals(count, histogram.getCount());
	}

	@Test
	public void testContext() throws InterruptedException {
		Histogram histogram = new Histogram();
		Histogram.Context ctx = histogram.time();
		TimeUnit.MILLISECONDS.sleep(11);
		ctx.stop();
		assertEquals(1, histogram.getCount());
		assertEquals(1, histogram.getU10());
		assertEquals(0, histogram.getU50());
		assertEquals(0, histogram.getU500());
	}

	@Test
	public void testContext_close() throws InterruptedException {
		Histogram histogram = new Histogram();
		Histogram.Context ctx = histogram.time();
		TimeUnit.MILLISECONDS.sleep(55);
		ctx.close();
		assertEquals(1, histogram.getCount());
		assertEquals(1, histogram.getU10());
		assertEquals(1, histogram.getU50());
		assertEquals(0, histogram.getU500());
	}


}
