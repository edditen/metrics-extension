package com.tenchael.metrics.extension.utils;

import com.tenchael.metrics.extension.support.Whitebox;
import org.junit.Assert;
import org.junit.Test;

public class NameUtilsTests extends Assert {

	@Test
	public void testConstruct() throws Exception {
		try {
			Whitebox.newInstance(NameUtils.class);
			assertFalse(true);
		} catch (Exception e) {
			e.printStackTrace();
			assertTrue(e.getCause() instanceof IllegalAccessException);
		}
	}

	@Test
	public void testName() {
		String prefix = "hello.com";
		String part1 = "world";
		String part2 = "come";
		String part3 = "on";
		String name = NameUtils.name(prefix, '#', part1, part2, part3);
		System.out.println(name);
		assertEquals("hello.com#world#come#on", name);
	}


}
