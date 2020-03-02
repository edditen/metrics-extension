package com.tenchael.metrics.extension.common;

import com.tenchael.metrics.extension.common.support.Whitebox;
import org.junit.Assert;
import org.junit.Test;

public class SwallowExceptionHandlerTests extends Assert {

	@Test
	public void testConstruct() throws Exception {
		try {
			Whitebox.newInstance(SwallowExceptionHandler.class);
			assertFalse(true);
		} catch (Exception e) {
			e.printStackTrace();
			assertTrue(e.getCause() instanceof IllegalAccessException);
		}
	}

	@Test
	public void testHandleException() {
		final Exception ex = new RuntimeException("something wrong");
		SwallowExceptionHandler.swallow(new SwallowExceptionListener() {
			@Override
			public void onException(String message, Throwable e) {
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
			public void onException(String message, Throwable e) {
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
			public void onException(String message, Throwable e) {
				throw new OutOfMemoryError("oom");
			}
		}, "oops", ex);
	}

	@Test(expected = VirtualMachineError.class)
	public void testHandleExceptionVirtualMachineError() {
		final Exception ex = new RuntimeException("something wrong");
		SwallowExceptionHandler.swallow(new SwallowExceptionListener() {
			@Override
			public void onException(String message, Throwable e) {
				throw new StackOverflowError("sof");
			}
		}, "oops", ex);
	}

	@Test
	public void testHandleExceptionThrowable() {
		final Exception ex = new RuntimeException("something wrong");
		SwallowExceptionHandler.swallow(new SwallowExceptionListener() {
			@Override
			public void onException(String message, Throwable e) {
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
