package com.tenchael.metrics.extension.common;

import com.tenchael.metrics.extension.common.support.Whitebox;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class UniformSwallowHolderTests extends Assert {

	private SwallowExceptionListener originListener;

	@Before
	public void setUp() {
		originListener = UniformSwallowHolder.getListener();
	}

	@After
	public void tearDown() {
		UniformSwallowHolder.setListener(originListener);
	}

	@Test
	public void testConstruct() throws Exception {
		try {
			Whitebox.newInstance(UniformSwallowHolder.class);
			assertFalse(true);
		} catch (Exception e) {
			e.printStackTrace();
			assertTrue(e.getCause() instanceof IllegalAccessException);
		}
	}

	@Test
	public void testDefaultListener() {
		SwallowExceptionListener listener = UniformSwallowHolder.getListener();
		assertEquals(SwallowExceptionListener.STDOUT, listener);
	}

	@Test
	public void testSetListener() {
		SwallowExceptionListener swallowExceptionListener = new SwallowExceptionListener() {

			@Override
			public void onException(String message, Throwable e) {
				//do nothing
			}
		};
		UniformSwallowHolder.setListener(swallowExceptionListener);
		SwallowExceptionListener listener = UniformSwallowHolder.getListener();
		assertEquals(swallowExceptionListener, listener);
	}

}
