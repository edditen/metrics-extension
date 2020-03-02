package com.tenchael.metrics.extension.common;

import com.tenchael.metrics.extension.common.support.ChineseGreeting;
import com.tenchael.metrics.extension.common.support.GreetingService;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

/**
 * Created by Tenchael on 2020/2/25.
 */
public class ExtensionLoaderTests extends Assert {

	@Test
	public void testGetExtensionLoader() {
		List<GreetingService> services = ExtensionLoader.getExtensionLoader(GreetingService.class)
				.getServices();
		assertEquals(2, services.size());
	}

	@Test
	public void testGetExtensionLoader2() {
		List<GreetingService> services = ExtensionLoader.getExtensionLoader(GreetingService.class)
				.getServices();
		assertTrue(services.get(0) instanceof ChineseGreeting);
	}


}
