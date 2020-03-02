package com.tenchael.metrics.extension.common.utils;


import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsString;

public class NamedThreadFactoryTest extends Assert {
	@Test
	public void testNewThread() throws Exception {
		NamedThreadFactory factory = new NamedThreadFactory();
		Thread t = factory.newThread(Mockito.mock(Runnable.class));
		assertThat(t.getName(), allOf(containsString("pool-"), containsString("-thread-")));
		assertFalse(t.isDaemon());
		// since security manager is not installed.
		assertSame(t.getThreadGroup(), Thread.currentThread().getThreadGroup());
		assertNotNull(factory.getThreadGroup());
	}

	@Test
	public void testPrefixAndDaemon() throws Exception {
		NamedThreadFactory factory = new NamedThreadFactory("prefix", true);
		Thread t = factory.newThread(Mockito.mock(Runnable.class));
		assertThat(t.getName(), allOf(containsString("prefix-"), containsString("-thread-")));
		assertTrue(t.isDaemon());
	}
}
