package com.tenchael.metrics.extension.utils;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class UniformSwallowHolderTests {

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
	public void testDefaultListener() {
		SwallowExceptionListener listener = UniformSwallowHolder.getListener();
		assertEquals(SwallowExceptionListener.STDOUT, listener);
	}

	@Test
	public void testSetListener() {
		SwallowExceptionListener swallowExceptionListener = new SwallowExceptionListener() {

			@Override
			public void onException(String message, Exception e) {
				//do nothing
			}
		};
		UniformSwallowHolder.setListener(swallowExceptionListener);
		SwallowExceptionListener listener = UniformSwallowHolder.getListener();
		assertEquals(swallowExceptionListener, listener);
	}

}
