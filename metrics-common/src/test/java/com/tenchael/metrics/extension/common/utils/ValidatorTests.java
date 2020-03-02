package com.tenchael.metrics.extension.common.utils;

import com.tenchael.metrics.extension.common.support.Whitebox;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by Tenchael on 2020/2/25.
 */
public class ValidatorTests extends Assert {

	@Test
	public void testConstruct() throws Exception {
		try {
			Whitebox.newInstance(Validator.class);
			assertFalse(true);
		} catch (Exception e) {
			e.printStackTrace();
			assertTrue(e.getCause() instanceof IllegalAccessException);
		}
	}


	@Test
	public void testNotNull() {
		Integer data = 12;
		Validator.notNull(data, "data can not be null");
	}

	@Test(expected = IllegalArgumentException.class)
	public void testNotNull2() {
		Integer data = null;
		try {
			Validator.notNull(data, "data can not be null");
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	@Test(expected = IllegalArgumentException.class)
	public void testNotNull3() {
		Integer data = null;
		try {
			Validator.notNull(data, "data %s can not be null", "hhh");
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	@Test(expected = IllegalArgumentException.class)
	public void testIsTrue() {
		int d = 6;
		Validator.isTrue(d % 2 == 0, "data %d must be even", d);

		try {
			Validator.isTrue(d % 2 == 1, "data %d must be even", d);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
}
