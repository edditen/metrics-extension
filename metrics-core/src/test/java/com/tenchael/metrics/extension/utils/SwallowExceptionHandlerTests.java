package com.tenchael.metrics.extension.utils;

import org.junit.Assert;
import org.junit.Test;

public class SwallowExceptionHandlerTests extends Assert {

	@Test
	public void testHandleException() {
		final Exception ex = new RuntimeException("something wrong");
		SwallowExceptionHandler.swallow(new SwallowExceptionListener() {
			@Override
			public void onException(String message, Exception e) {
				assertNull(message);
				assertEquals(ex, e);
			}
		}, ex);
	}

	@Test
	public void testHandleExceptionWithMessage() {
		final Exception ex = new RuntimeException("something wrong");
		SwallowExceptionHandler.swallow(new SwallowExceptionListener() {
			@Override
			public void onException(String message, Exception e) {
				assertEquals("oops", message);
				assertEquals(ex, e);
			}
		}, "oops", ex);
	}

	@Test(expected = OutOfMemoryError.class)
	public void testHandleExceptionOOM() {
		final Exception ex = new RuntimeException("something wrong");
		SwallowExceptionHandler.swallow(new SwallowExceptionListener() {
			@Override
			public void onException(String message, Exception e) {
				throw new OutOfMemoryError("oom");
			}
		}, "oops", ex);
	}

	@Test(expected = VirtualMachineError.class)
	public void testHandleExceptionVirtualMachineError() {
		final Exception ex = new RuntimeException("something wrong");
		SwallowExceptionHandler.swallow(new SwallowExceptionListener() {
			@Override
			public void onException(String message, Exception e) {
				throw new StackOverflowError("sof");
			}
		}, "oops", ex);
	}

	@Test
	public void testHandleExceptionThrowable() {
		final Exception ex = new RuntimeException("something wrong");
		SwallowExceptionHandler.swallow(new SwallowExceptionListener() {
			@Override
			public void onException(String message, Exception e) {
				throw new RuntimeException("runtime");
			}
		}, "oops", ex);
		assertTrue(true);
	}

	@Test
	public void testHandleExceptionWithDefault() {
		final Exception ex = new RuntimeException("something wrong");
		SwallowExceptionHandler.swallow(ex);
		SwallowExceptionHandler.swallow("hello", ex);
		SwallowExceptionHandler.swallow(null, "hello", ex);
		SwallowExceptionHandler.swallow(SwallowExceptionListener.STDOUT, "hello", ex);
		SwallowExceptionHandler.swallow(SwallowExceptionListener.STDOUT, ex);
	}
}
