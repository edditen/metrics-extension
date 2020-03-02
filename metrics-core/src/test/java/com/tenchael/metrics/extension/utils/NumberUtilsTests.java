package com.tenchael.metrics.extension.utils;

import com.tenchael.metrics.extension.support.Whitebox;
import org.junit.Assert;
import org.junit.Test;

public class NumberUtilsTests extends Assert {

	@Test
	public void testConstruct() throws Exception {
		try {
			Whitebox.newInstance(NumberUtils.class);
			assertFalse(true);
		} catch (Exception e) {
			e.printStackTrace();
			assertTrue(e.getCause() instanceof IllegalAccessException);
		}
	}

	@Test
	public void testDecimalPrecise() {
		double d = NumberUtils.decimalPrecise(1.2345678f, 2);
		assertEquals(1.23, d, 0.001f);
		d = NumberUtils.decimalPrecise(1.235678f, 2);
		assertEquals(1.24, d, 0.001f);
		d = NumberUtils.decimalPrecise(16.235678f, 2);
		assertEquals(16.24, d, 0.001f);
		d = NumberUtils.decimalPrecise(Double.POSITIVE_INFINITY, 2);
		assertEquals(0.0f, d, 0.001f);

	}

}
